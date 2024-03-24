package org.housemate.domain.repositories

interface AuthRepository {
    suspend fun login(email: String, password:String):Boolean
    suspend fun register(email:String, password: String):Boolean
    suspend fun logout(): Boolean
    suspend fun getLoginState():Boolean
    suspend fun deleteAccount(): Boolean
}
