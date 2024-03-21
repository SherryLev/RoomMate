package org.housemate.domain.repositories

import org.housemate.domain.model.Chore
import com.google.android.gms.tasks.Task
import org.housemate.data.firestore.ChoreRepositoryImpl

interface ChoreRepository {
    fun createChore(chore: Chore): Task<Void>
    fun getChoresByUserId(userId: String): Task<List<Chore>>
    fun updateChore(chore: Chore): Task<Void>
    fun deleteChore(choreId: String, userId: String): Task<Void>
}