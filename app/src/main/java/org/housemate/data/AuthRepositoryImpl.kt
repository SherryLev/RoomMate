package org.housemate.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.housemate.domain.repositories.AuthRepository


class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth = Firebase.auth
    override suspend fun register(email: String, password: String): Boolean {
        try {
            firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            ).await()
            delay(700)
            Log.d("main", "User id ${firebaseAuth.currentUser?.uid} created successfully")
            return true

        } catch (e: Exception) {
                Log.d("main", "Failed to register user.")
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
