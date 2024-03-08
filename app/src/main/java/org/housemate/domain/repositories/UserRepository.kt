package org.housemate.domain.repositories

import org.housemate.domain.model.User
interface UserRepository {
    suspend fun addUser(user: User): Boolean
    suspend fun getUserById(userId: String): User?
}