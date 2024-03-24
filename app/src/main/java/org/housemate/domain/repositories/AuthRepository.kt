package org.housemate.domain.repositories

import org.housemate.presentation.viewmodel.DeleteAccountResult

interface AuthRepository {
    suspend fun login(email: String, password:String):Boolean
    suspend fun register(email:String, password: String):Boolean
    suspend fun logout(): Boolean
    suspend fun getLoginState():Boolean
    suspend fun deleteAccount(userPassword: String): DeleteAccountResult
}
