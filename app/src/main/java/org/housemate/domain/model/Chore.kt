package org.housemate.domain.model

import java.time.LocalDateTime

data class Chore(
    val choreId: String,
    val choreName: String,
    val assignee: String,
    val category: String,
    val dueDate: LocalDateTime? = null,
    val userRating : List<Int>?, // nullable
    val votedUser: List<String>? // nullable
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "choreId" to choreId,
            "choreName" to choreName,
            "assignee" to assignee,
            "category" to category,
            "dueDate" to dueDate?.toString(),
            "userRating" to userRating,
            "votedUser" to votedUser
        )
    }
}