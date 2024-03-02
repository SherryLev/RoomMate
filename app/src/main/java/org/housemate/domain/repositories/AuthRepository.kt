package org.housemate.domain.repositories

interface AuthRepository {
    suspend fun login(email: String, password:String):Boolean
    suspend fun register(email:String, password: String):Boolean

}