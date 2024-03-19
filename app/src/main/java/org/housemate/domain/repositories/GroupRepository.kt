package org.housemate.domain.repositories

import org.housemate.domain.model.Group

interface GroupRepository {
    suspend fun CreateGroup(group: Group): Boolean
    suspend fun addMemberToGroup(groupCode: String, memberId: String): Boolean
    suspend fun getGroupByCode(groupCode: String): Group?
}