package org.housemate.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User (
    val email: String = "",
    val uId: String = "",
    val groupCode: String? = null // nullable since when a user joins they are not in a gorup
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uId" to uId,
            "email" to email,
            "groupCode" to groupCode
        )
    }
}