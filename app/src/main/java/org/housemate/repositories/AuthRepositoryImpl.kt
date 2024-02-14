package org.housemate.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.housemate.utils.AuthResultState
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor (
    private val firebaseAuth:FirebaseAuth
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<AuthResultState<AuthResult>> {
        return flow {
            emit(value = AuthResultState.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(value = AuthResultState.Success(data = result))
        }.catch {
            emit(value = AuthResultState.Error(it.message.toString()))
        }
    }

    override fun registerUser(email: String, password: String): Flow<AuthResultState<AuthResult>> {
        return flow {
            emit(value = AuthResultState.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(value = AuthResultState.Success(data = result))
        }.catch {
            emit(value = AuthResultState.Error(it.message.toString()))
        }
    }
}