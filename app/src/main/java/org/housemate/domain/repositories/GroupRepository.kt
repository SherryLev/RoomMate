package org.housemate.domain.repositories

import org.housemate.domain.model.Group
import org.housemate.domain.model.User

interface GroupRepository {
    suspend fun createGroup(group: Group): Boolean
    suspend fun addMemberToGroup(groupCode: String, memberId: String): Boolean
    suspend fun getGroupByCode(groupCode: String): Group?
    suspend fun removeMemberFromGroup(groupCode: String, userId: String): Boolean
    suspend fun fetchAllGroupMembers(userId: String): List<User>?
}