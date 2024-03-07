package org.housemate.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.housemate.domain.repositories.AuthRepository

import com.google.firebase.firestore.FirebaseFirestore


class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    override suspend fun register(email: String, password: String): Boolean {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            authResult.user?.let { firebaseUser ->
                val userData = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to email
                )

                // Sotre user details in Firestore
                db.collection("users").document(firebaseUser.uid).set(userData).await()
                Log.d(
                    "main",
                    "User id ${firebaseUser} created successfully and details stored in Firestore"
                )
                return true
            } ?: run {
                Log.d("main", "User registration succeeded but user data is null.")
                return false
            }
        } catch (e: Exception) {
                Log.d("main", "Failed to register user or store user details.", e)
                return false
        }
    }


    override suspend fun login(email: String, password: String): Boolean {
        try {
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            ).await()
            delay(700)
            Log.d("main", "User id ${firebaseAuth.currentUser?.uid} logged in successfully")
            return true
        } catch (e: Exception) {
            Log.d("main", "Failed to log user in.")
            return false
        }
    }
}
