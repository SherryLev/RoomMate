package org.housemate.domain.repositories

import org.housemate.domain.model.Group

interface GroupRepository {
    suspend fun createGroup(group: Group): Boolean
    suspend fun addMemberToGroup(groupCode: String, memberId: String): Boolean
    suspend fun getGroupByCode(groupCode: String): Group?
    suspend fun removeMemberFromGroup(groupCode: String, userId: String): Boolean
    suspend fun isCreator(userId: String, groupCode: String): Boolean
    suspend fun createGroupName(groupCode: String, groupName: String): Boolean
    suspend fun getGroupName(groupCode: String): String?
}