package org.housemate.data

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.housemate.domain.repositories.AuthRepository

import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.domain.model.User
import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.repositories.UserRepository
import org.housemate.presentation.viewmodel.DeleteAccountResult


class AuthRepositoryImpl (
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
)  : AuthRepository {

    override suspend fun register(email: String, username: String, password: String): Boolean {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()

            // Add user to Firestore if not already added
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userFromFirestore = userRepository.getUserById(currentUser.uid)
                if (userFromFirestore == null) {
                    // User doesn't exist in Firestore, so add them
                    val newUser = User(
                        uid = currentUser.uid,
                        username = username,
                        email = email,
                        loggedIn = true
                    )
                    userRepository.addUser(newUser)
                }
            }
            Log.d(
                "main",
                "User id $currentUser created successfully and details stored in Firestore"
            )
            return true

        } catch (e: Exception) {
                Log.d("main", "Failed to register user or store user details.", e)
                return false
        }
    }


    override suspend fun login(email: String, password: String): Boolean {
        try {
            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()
            delay(600)
            val currentUser = auth.currentUser
            currentUser?.let { firebaseUser ->
                // Update isLoggedIn field in Firestore for the logged-in user
                firestore.collection("users").document(firebaseUser.uid)
                    .update("loggedIn", true)
                    .await()
                Log.d(
                    "main",
                    "User id ${firebaseUser.uid} logged in successfully and isLoggedIn field updated in Firestore"
                )
                return true
            }
            Log.d("main", "Failed to log user in.")
            return false
        } catch (e: Exception) {
            Log.d("main", "Failed to log user in.")
            return false
        }
    }
    override suspend fun logout(): Boolean {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let { user ->
                val userId = user.uid
                // Update isLoggedIn field to false in Firestore
                firestore.collection("users").document(userId)
                    .update("loggedIn", false)
                    .await()
                return true
            }
            Log.e("AuthRepository", "No user logged in.")
            return false
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging out user", e)
            return false
        }
    }
    override suspend fun getLoginState(): Boolean {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userDocument =
                    firestore.collection("users").document(currentUser.uid).get().await()
                return userDocument.getBoolean("loggedIn") ?: false
            } else {
                // If currentUser is null, the user is not logged in
                return false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting login state", e)
            return false
        }
    }

    override suspend fun deleteAccount(userPassword: String): DeleteAccountResult {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, userPassword)
                currentUser.reauthenticate(credential).await()

                val userId = currentUser.uid
                // delete them from the users collection
                userRepository.deleteUserById(userId)
                // remove them from the group
                val currentGroupCode = userRepository.getGroupCodeForUser(userId)
                if (currentGroupCode != null) {
                    groupRepository.removeMemberFromGroup(currentGroupCode, userId)
                }
                currentUser.delete().await()
                return DeleteAccountResult.Success
            } else {
                Log.e("AuthRepository", "No user logged in.")
                return DeleteAccountResult.Error
            }
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Log.e("AuthRepository", "User needs to reauthenticate", e)
            return DeleteAccountResult.Error
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Incorrect password provided by the user
            Log.e("AuthRepository", "Incorrect password", e)
            return DeleteAccountResult.IncorrectPassword
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error deleting account", e)
            return DeleteAccountResult.Error
        }
    }
}
