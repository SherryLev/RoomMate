package org.housemate.data.firestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Group
import org.housemate.domain.repositories.GroupRepository
import android.util.Log

class GroupRepositoryImpl : GroupRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createGroup(group: Group): Boolean {
        return try {
            db.collection("groups").document(group.groupCode).set(
                hashMapOf(
                    "groupName" to group.groupName,
                    "creatorId" to group.creatorId,
                    "members" to group.members
                )
            ).await()
            true
        } catch (e: Exception){
            Log.e("CreateGroup", "Failed to create group", e)
            false
        }
    }

    override suspend fun addMemberToGroup(groupCode: String, memberId: String): Boolean {
        return try {
            val groupRef = db.collection("groups").document(groupCode)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(groupRef)
                val members = snapshot.get("members") as List<String>? ?: listOf()
                if (memberId !in members) {
                    transaction.update(groupRef, "members", members + memberId)
                }
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getGroupByCode(groupCode: String): Group? {
        return try {
            val snapshot = db.collection("groups").document(groupCode).get().await()
            if (snapshot.exists()) {
                val groupName = snapshot.getString("groupName") ?: ""
                val creatorId = snapshot.getString("creatorId") ?: ""
                val members = snapshot.get("members") as List<String>? ?: listOf()
                Group(groupCode, groupName, creatorId, members)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun removeMemberFromGroup(groupCode: String, userId: String): Boolean {
        return try {
            val groupRef = db.collection("groups").document(groupCode)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(groupRef)
                val currentMembers = snapshot.get("members") as? List<String> ?: listOf()
                if (userId in currentMembers) {
                    val updatedMembers = currentMembers - userId
                    transaction.update(groupRef, "members", updatedMembers)
                }
            }.await()
            true
        } catch (e: Exception) {
            Log.e("RemoveMember", "Failed to remove member from group", e)
            false
        }
    }

    override suspend fun isCreator(userId: String, groupCode: String): Boolean {
        return try {
            val documentSnapshot = db.collection("groups").document(groupCode).get().await()
            val creatorId = documentSnapshot.getString("creatorId")
            userId == creatorId
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error checking creator status")
            false
        }
    }

    override suspend fun createGroupName(groupCode: String, groupName: String): Boolean {
        return try {
            val groupRed = db.collection("groups").document(groupCode)
            db.runTransaction{ transaction ->
                transaction.update(groupRed, "groupName", groupName)
            }.await()
            true
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error creating group name")
            false
        }
    }

    override suspend fun getGroupName(groupCode: String): String? {
        return try {
            val snapshot = db.collection("groups").document(groupCode).get().await()
            if (snapshot.exists()) {
                snapshot.getString("groupName")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group name", e)
            null
        }
    }

}