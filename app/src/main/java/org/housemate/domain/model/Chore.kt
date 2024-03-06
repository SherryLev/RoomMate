package org.housemate.domain.model

import java.time.LocalDateTime

data class Chore(
    val id: Int,
    var choreName: String = "None",
    var category: String = "None",
    var assignee: String = "None",
    var dueDate: LocalDateTime? = null,
)