package org.housemate.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.housemate.model.User

class AuthRepositoryImpl () : AuthRepository {
    private val firebaseAuth = Firebase.auth
    override fun createUser(auth: User) {
        firebaseAuth.createUserWithEmailAndPassword(
            auth.email,
            auth.password
        ).addOnCompleteListener {
            if(it.isSuccessful) {
                Log.d("main", "current user id: ${firebaseAuth.currentUser?.uid}")
            }
        }
    }

    override fun loginUser(auth: User) {
        firebaseAuth.signInWithEmailAndPassword(
            auth.email,
            auth.password
        ).addOnSuccessListener {
            Log.d("main", "current user id: ${firebaseAuth.currentUser?.uid}")
        }.addOnFailureListener {
            Log.d("main", "user login failed")
        }
    }
}