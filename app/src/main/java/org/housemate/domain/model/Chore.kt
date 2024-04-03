package org.housemate.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Chore(
    val userId: String,
    val choreId: String,
    val choreName: String,
    val assignee: String,
    val category: String,
    val dueDate: LocalDate? = null,
    val userRating :  List<Int> = emptyList(),  // nullable
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