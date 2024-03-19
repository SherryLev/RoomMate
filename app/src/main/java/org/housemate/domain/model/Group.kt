package org.housemate.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Group(
    val groupCode: String,
    val groupName: String,
    val creatorId: String,
    val members: List<String>
)