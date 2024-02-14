package org.housemate.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User (
    val userId: String = "",
    val username: String = "",
    @ServerTimestamp
    var registrationDate: Date? = null
)