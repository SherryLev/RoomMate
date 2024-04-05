package org.housemate.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Chore
import org.housemate.domain.repositories.ChoreRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

class ChoreRepositoryImpl (
    private val auth: FirebaseAuth
) : ChoreRepository  {
    private val db = FirebaseFirestore.getInstance()

    override fun createChore(chore: Chore): Task<Void> {
        return db.collection("chores")
            .document(chore.assigneeId)
            .collection("userChores")
            .document(chore.choreId)
            .set(chore.toMap())
    }

    override fun getChoresByUserId(userId: String): Task<List<Chore>> {
        val choreList = mutableListOf<Chore>()
        val taskCompletionSource = TaskCompletionSource<List<Chore>>()

        db.collection("chores")
            .document(userId)
            .collection("userChores")
            .get()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val chore = document.toObject(Chore::class.java).copy(choreId = document.id)
                        choreList.add(chore)
                    }
                    taskCompletionSource.setResult(choreList)
                }
                else {
                    task.exception?.let {
                        taskCompletionSource.setException(it)
                    }
            }
        }

        return taskCompletionSource.task
    }

//    override fun getAllChores(): Task<List<Chore>> {
//        val choreList = mutableListOf<Chore>()
//        val taskCompletionSource = TaskCompletionSource<List<Chore>>()
//
//        db.collectionGroup("userChores")
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    for (document in task.result) {
//                        val chore = document.toObject(Chore::class.java).copy(choreId = document.id)
//                        choreList.add(chore)
//                    }
//                    taskCompletionSource.setResult(choreList)
//                } else {
//                    task.exception?.let {
//                        taskCompletionSource.setException(it)
//                    }
//                }
//            }
//
//        return taskCompletionSource.task
//    }

    override fun getAllChores(): Task<List<Chore>> {
        println("ingetallchores")
        val choreList = mutableListOf<Chore>()
        val taskCompletionSource = TaskCompletionSource<List<Chore>>()

        // Retrieve the current user from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            // Assuming the user's ID is the same as the document ID in the users collection
            val currentUserId = user.uid

            // First, retrieve the current user's group code from the users collection
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument != null && userDocument.exists()) {
                        val groupCode = userDocument.getString("groupCode")
                        if (groupCode != null) {
                            // Now fetch the list of member user IDs from the groups collection
                            db.collection("groups")
                                .document(groupCode)
                                .get()
                                .addOnSuccessListener { groupDocument ->
                                    if (groupDocument != null && groupDocument.exists()) {
                                        val members = groupDocument.get("members") as? List<*>
                                        members?.let { groupMembers ->
                                            // Fetch chores for each user within the group
                                            val fetchChoresTasks = groupMembers.map { memberId ->
                                                db.collection("chores")
                                                    .document(memberId.toString())
                                                    .collection("userChores")
                                                    .get()
                                                    .addOnSuccessListener { querySnapshot ->
                                                        for (document in querySnapshot) {
                                                            val chore = document.toObject(Chore::class.java).copy(choreId = document.id)
                                                            choreList.add(chore)
                                                        }
                                                    }
                                            }
                                            // Wait for all fetch chores tasks to complete
                                            Tasks.whenAllSuccess<Void>(fetchChoresTasks)
                                                .addOnSuccessListener {
                                                    taskCompletionSource.setResult(choreList)
                                                }
                                                .addOnFailureListener { exception ->
                                                    taskCompletionSource.setException(exception)
                                                }
                                        }
                                    } else {
                                        taskCompletionSource.setException(IllegalArgumentException("Group document not found"))
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    taskCompletionSource.setException(exception)
                                }
                        } else {
                            taskCompletionSource.setException(IllegalArgumentException("Group code not found for user"))
                        }
                    } else {
                        taskCompletionSource.setException(IllegalArgumentException("User document not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
        } ?: run {
            taskCompletionSource.setException(IllegalStateException("Current user is null"))
        }

        return taskCompletionSource.task
    }


    override fun updateChore(chore: Chore): Task<Void> {
        val choreMap = chore.toMap()

        return db.collection("chores")
            .document(chore.assignee)
            .collection("userChores")
            .document(chore.choreId)
            .update(choreMap)

    }

    override fun deleteChore(choreId: String, userId: String): Task<Void> {
        return db.collection("chores")
            .document(userId)
            .collection("userChores")
            .document(choreId)
            .delete()
    }
}