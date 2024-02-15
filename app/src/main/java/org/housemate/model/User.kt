package org.housemate.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User (
    val email: String = "",
    val password: String = ""
)