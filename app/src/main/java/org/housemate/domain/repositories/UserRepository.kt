package org.housemate.domain.repositories

import org.housemate.domain.model.User
interface UserRepository {
    suspend fun addUser(user: User): Boolean
    suspend fun getUserById(userId: String): User?
    suspend fun getCurrentUserId():String?

    suspend fun deleteUserById(userId: String): Boolean

    suspend fun getGroupCodeForUser(userId: String): String?

    suspend fun updateUserGroupCode(userId: String, newGroupCode: String): Boolean
    }