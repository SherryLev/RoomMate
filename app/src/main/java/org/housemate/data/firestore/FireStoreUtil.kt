package org.housemate.data.firestore
// FirestoreUtil.kt
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.utils.GroupUtil

object FireStoreUtil {
    private val db = FirebaseFirestore.getInstance()

    // creating a group in firestore
    fun createGroup(groupName: String, creatorId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val groupCode = GroupUtil.generateUniqueCode()
        val groupInfo = hashMapOf(
            "name" to groupName,
            "creator" to creatorId,
            "members" to listOf(creatorId) // Initially the creator is the only member
        )

        // Adding the new group to firestore
        db.collection("groups").document(groupCode).set(groupInfo)
            .addOnSuccessListener {
                onSuccess(groupCode) // Pass group code back to UI
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Pass error back to UI
            }
    }

    // adding a member to a group in the firestore using group code
    fun addMemberToGroup(groupCode: String, newMemberId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val groupRef = db.collection("groups").document(groupCode)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(groupRef)
            val currentMembers = snapshot.get("members") as List<String>?: listOf()
            if (!currentMembers.contains(newMemberId)) {
                val updateMembers = currentMembers + newMemberId
                transaction.update(groupRef, "members", updateMembers)
            }
        }.addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception)}
    }
}