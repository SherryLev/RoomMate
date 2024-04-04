package org.housemate.domain.model

data class Group(
    val groupCode: String,
    val groupName: String,
    val creatorId: String?,
    val members: List<String?>
)