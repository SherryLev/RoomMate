package org.housemate.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.housemate.domain.repositories.AuthRepository

import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.domain.model.User
import org.housemate.domain.repositories.UserRepository


class AuthRepositoryImpl (
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
)  : AuthRepository {

    override suspend fun register(email: String, password: String): Boolean {
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

    override suspend fun deleteAccount(): Boolean {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                // Delete user document from Firestore
                userRepository.deleteUserById(userId)
                // Delete user account from Firebase Authentication
                currentUser.delete().await()
                return true
            } else {
                Log.e("AuthRepository", "No user logged in.")
                return false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error deleting account", e)
            return false
        }
    }
}
