package org.housemate.domain.repositories

import org.housemate.domain.model.Chore
import com.google.android.gms.tasks.Task

interface ChoreRepository {
    fun createChore(chore: Chore): Task<Void>
    fun getGroupChores(): Task<List<Chore>>
    fun updateChore(chore: Chore): Task<Void>
    fun deleteChore(choreId: String, userId: String): Task<Void>
    fun deleteMultipleChores(chorePrefix: String, userId: String): Task<Void>
    fun updateChoreRating(chore: Chore, newRating: Float, userId: String): Task<Void>
    fun createDefaultChoresAndCategories(): Task<Void>
    fun addCategory(newCategory: String, userId: String): Task<Void>
    fun addChoreType(newChore: String, userId: String): Task<Void>
    fun getChoreTypes(): Task<List<String>>
    fun getChoreCategories(): Task<List<String>>

}