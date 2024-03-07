package org.housemate.data.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.User
import org.housemate.domain.repositories.UserRepository

class UserRepositoryImpl : UserRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addUser(user: User): Boolean {
        return try {
            db.collection("users").document(user.uId).set(user.toMap()).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            false
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try{
            val document = db.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user", e)
            null
        }
    }
}