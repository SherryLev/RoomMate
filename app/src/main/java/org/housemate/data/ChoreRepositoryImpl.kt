package org.housemate.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.domain.model.Chore
import org.housemate.domain.repositories.ChoreRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FieldPath
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class ChoreRepositoryImpl (
    private val auth: FirebaseAuth
) : ChoreRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun createChore(chore: Chore): Task<Void> {
        return db.collection("chores")
            .document(chore.assigneeId)
            .collection("userChores")
            .document(chore.choreId)
            .set(chore.toMap())
    }


    override fun getGroupChores(): Task<List<Chore>> {
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
                                                            val chore =
                                                                document.toObject(Chore::class.java)
                                                                    .copy(choreId = document.id)
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

    override fun deleteMultipleChores(chorePrefix: String, userId: String): Task<Void> {
        val completionSource = TaskCompletionSource<Void>()
        val batch = db.batch()

        db.collection("chores")
            .document(userId)
            .collection("userChores")
            .whereGreaterThan(FieldPath.documentId(), chorePrefix)
            .whereLessThan(FieldPath.documentId(), "$chorePrefix\uffff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        completionSource.setResult(null)
                    }
                    .addOnFailureListener { exception ->
                        completionSource.setException(exception)
                    }
            }
            .addOnFailureListener { exception ->
                completionSource.setException(exception)
            }

        return completionSource.task
    }

    override fun updateChoreRating(chore: Chore, newRating: Float, userId: String): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        db.collection("chores")
            .document(chore.assigneeId) // Assuming userId refers to the user's document ID
            .collection("userChores")
            .document(chore.choreId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val chore = documentSnapshot.toObject(Chore::class.java)

                    // Update the userRating for the current chore
                    val updatedUserRating = chore?.userRating?.toMutableMap() ?: mutableMapOf()
                    updatedUserRating[userId] = newRating
                    val updatedChore = chore?.copy(userRating = updatedUserRating)

                    if (updatedChore != null) {
                        db.collection("chores")
                            .document(chore.assigneeId)
                            .collection("userChores")
                            .document(chore.choreId)
                            .update(updatedChore.toMap())
                            .addOnSuccessListener {
                                taskCompletionSource.setResult(null)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("updateChoreRating", "Update failed: $exception")
                                taskCompletionSource.setException(exception)
                            }
                    } else {
                        val exception = Exception("Chore object is null")
                        Log.e("updateChoreRating", "Chore object is null")
                        taskCompletionSource.setException(exception)
                    }
                } else {
                    val exception = Exception("Chore document not found")
                    Log.e("updateChoreRating", "Chore document not found")
                    taskCompletionSource.setException(exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("updateChoreRating", "Query failed: $exception")
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }


    override fun createDefaultChoresAndCategories(): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val currentUserId = user.uid

            // First, retrieve the current user's group code from the users collection
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument != null && userDocument.exists()) {
                        val groupCode = userDocument.getString("groupCode")

                        // Check if the document already exists for the group code
                        db.collection("group_chore_categories")
                            .document(groupCode!!)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    // Document already exists, do nothing
                                    taskCompletionSource.setResult(null)
                                } else {
                                    // Define default categories
                                    val categoryList = listOf("Kitchen", "Living Room", "Dining Room", "Bathroom", "Stairs", "Backyard", "Front Yard", "Shower")

                                    // Define default chores
                                    val choresList = listOf("Clean dishes", "Clean counters", "Clean stove", "Clean fridge", "Sweep Floors", "Vacuum Floor", "Clean Toilet")

                                    // Set the document ID as the group code
                                    val groupChoreCategoriesRef = db.collection("group_chore_categories").document(groupCode)

                                    val data = hashMapOf(
                                        "category_list" to categoryList,
                                        "chores_list" to choresList
                                    )

                                    groupChoreCategoriesRef.set(data)
                                        .addOnSuccessListener {
                                            taskCompletionSource.setResult(null)
                                        }
                                        .addOnFailureListener { exception ->
                                            taskCompletionSource.setException(exception)
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                taskCompletionSource.setException(exception)
                            }
                    } else {
                        taskCompletionSource.setException(Exception("User document not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
        } ?: taskCompletionSource.setException(Exception("Current user is null"))

        return taskCompletionSource.task
    }


    override fun addCategory(newCategory: String, userId: String): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        // Retrieve the current user's group code from the users collection
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument != null && userDocument.exists()) {
                    val groupCode = userDocument.getString("groupCode")

                    if (groupCode != null) {
                        // Get the document reference for the group code
                        val groupChoreCategoriesRef = db.collection("group_chore_categories").document(groupCode)

                        // Update the category list in the document
                        groupChoreCategoriesRef.update("category_list", FieldValue.arrayUnion(newCategory))
                            .addOnSuccessListener {
                                taskCompletionSource.setResult(null)
                            }
                            .addOnFailureListener { exception ->
                                taskCompletionSource.setException(exception)
                            }
                    } else {
                        taskCompletionSource.setException(Exception("Group code not found for the user"))
                    }
                } else {
                    taskCompletionSource.setException(Exception("User document not found"))
                }
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }

    override fun addChoreType(newChore: String, userId: String): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        // Retrieve the current user's group code from the users collection
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument != null && userDocument.exists()) {
                    val groupCode = userDocument.getString("groupCode")

                    if (groupCode != null) {
                        // Get the document reference for the group code
                        val groupChoreCategoriesRef = db.collection("group_chore_categories").document(groupCode)

                        // Update the chore list in the document
                        groupChoreCategoriesRef.update("chores_list", FieldValue.arrayUnion(newChore))
                            .addOnSuccessListener {
                                taskCompletionSource.setResult(null)
                            }
                            .addOnFailureListener { exception ->
                                taskCompletionSource.setException(exception)
                            }
                    } else {
                        taskCompletionSource.setException(Exception("Group code not found for the user"))
                    }
                } else {
                    taskCompletionSource.setException(Exception("User document not found"))
                }
            }
            .addOnFailureListener { exception ->
                taskCompletionSource.setException(exception)
            }

        return taskCompletionSource.task
    }

    override fun getChoreTypes(): Task<List<String>> {
        val taskCompletionSource = TaskCompletionSource<List<String>>()

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val currentUserId = user.uid

            // Retrieve the current user's group code from the users collection
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument != null && userDocument.exists()) {
                        val groupCode = userDocument.getString("groupCode")

                        if (groupCode != null) {
                            // Get the document reference for the group code
                            val groupChoreCategoriesRef = db.collection("group_chore_categories").document(groupCode)

                            // Fetch the chore types from the document
                            groupChoreCategoriesRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        val choreTypes = documentSnapshot.get("chores_list") as? List<String> ?: emptyList()
                                        taskCompletionSource.setResult(choreTypes)
                                    } else {
                                        taskCompletionSource.setException(Exception("Document for group code not found"))
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    taskCompletionSource.setException(exception)
                                }
                        } else {
                            taskCompletionSource.setException(Exception("Group code not found for the user"))
                        }
                    } else {
                        taskCompletionSource.setException(Exception("User document not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
        } ?: taskCompletionSource.setException(Exception("Current user is null"))

        return taskCompletionSource.task
    }

    override fun getChoreCategories(): Task<List<String>> {
        val taskCompletionSource = TaskCompletionSource<List<String>>()

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val currentUserId = user.uid

            // Retrieve the current user's group code from the users collection
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument != null && userDocument.exists()) {
                        val groupCode = userDocument.getString("groupCode")

                        if (groupCode != null) {
                            // Get the document reference for the group code
                            val groupChoreCategoriesRef = db.collection("group_chore_categories").document(groupCode)

                            // Fetch the chore categories from the document
                            groupChoreCategoriesRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        val choreCategories = documentSnapshot.get("category_list") as? List<String> ?: emptyList()
                                        taskCompletionSource.setResult(choreCategories)
                                    } else {
                                        taskCompletionSource.setException(Exception("Document for group code not found"))
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    taskCompletionSource.setException(exception)
                                }
                        } else {
                            taskCompletionSource.setException(Exception("Group code not found for the user"))
                        }
                    } else {
                        taskCompletionSource.setException(Exception("User document not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    taskCompletionSource.setException(exception)
                }
        } ?: taskCompletionSource.setException(Exception("Current user is null"))

        return taskCompletionSource.task
    }


}
