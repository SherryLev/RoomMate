package org.housemate.data.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.User
import org.housemate.domain.repositories.UserRepository

class UserRepositoryImpl (
    private val firestore : FirebaseFirestore
): UserRepository {

    override suspend fun addUser(user: User): Boolean {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            false
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try{
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user", e)
            null
        }
    }

    override suspend fun getCurrentUserId():String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}