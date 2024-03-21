package org.housemate.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Chore
import org.housemate.domain.repositories.ChoreRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class ChoreRepositoryImpl : ChoreRepository{
    private val db = FirebaseFirestore.getInstance()

    override fun createChore(chore: Chore): Task<Void> {
        return db.collection("chores")
            .document(chore.userId)
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