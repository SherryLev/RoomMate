package org.housemate.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import org.housemate.domain.model.User
import org.housemate.domain.repositories.AuthRepository


class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth = Firebase.auth
    override suspend fun register(email: String, password: String) : Boolean {
//        firebaseAuth.createUserWithEmailAndPassword(
//            email,
//            password
//        ).addOnCompleteListener {
//            if(it.isSuccessful) {
//                Log.d("main", "current user id: ${firebaseAuth.currentUser?.uid}")
//            }
        delay(1000)
        return true
    }

    override suspend fun login(email: String, password: String) : Boolean {
//        firebaseAuth.signInWithEmailAndPassword(
//            email,
//            password
//        ).addOnSuccessListener {
//            Log.d("main", "current user id: ${firebaseAuth.currentUser?.uid}")
//        }.addOnFailureListener {
//            Log.d("main", "user login failed")
//        }
        delay(1000)
        return true
    }
}
