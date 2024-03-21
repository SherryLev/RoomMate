package org.housemate.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User (
    val email: String = "",
    val uid: String = "",
    val username: String = "",
    val groupCode: String? = null, // nullable since when a user joins they are not in a gorup
    val isLoggedIn: Boolean = false
)