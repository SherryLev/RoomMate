package org.housemate.domain.model

import com.google.firebase.Timestamp

sealed interface ExpenseOrPayment {
    val timestamp: Timestamp
    val amount: Double
}

