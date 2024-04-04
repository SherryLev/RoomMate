package org.housemate.domain.model

import com.google.firebase.Timestamp

data class Chore(
    val userId: String,
    val choreId: String,
    val choreName: String,
    val assignee: String,
    val category: String,
    val dueDate: Timestamp? = null,
    val userRating :  List<Int> = emptyList(),  // nullable
    val votedUser: List<String>?, // nullable
    val repeat: String
) {
    constructor() : this("", "", "", "", "", null, emptyList(), null, "")
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "choreId" to choreId,
            "choreName" to choreName,
            "assignee" to assignee,
            "category" to category,
            "dueDate" to dueDate,
            "userRating" to userRating,
            "votedUser" to votedUser,
            "repeat" to repeat
        )
    }
}