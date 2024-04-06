package org.housemate.domain.model

import com.google.firebase.Timestamp

data class Chore(
    val userId: String,
    val choreId: String,
    val choreName: String,
    val assignee: String,
    val assigneeId: String,
    val category: String,
    val dueDate: Timestamp? = null,
    val userRating : Map<String, Float> = emptyMap(),  // nullable
    val repeat: String
) {
    constructor() : this("", "", "", "", "", "", null, emptyMap(), "")
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "choreId" to choreId,
            "choreName" to choreName,
            "assignee" to assignee,
            "assigneeId" to assigneeId,
            "category" to category,
            "dueDate" to dueDate,
            "userRating" to userRating,
            "repeat" to repeat
        )
    }
}