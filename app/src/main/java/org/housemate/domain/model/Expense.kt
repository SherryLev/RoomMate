package org.housemate.domain.model

import com.google.firebase.Timestamp

data class Expense(
    val payer: String = "",
    val description: String = "",
    override val amount: Double = 0.0,
    val owingAmounts: Map<String, Double> = emptyMap(),
    override val timestamp: Timestamp = Timestamp.now()
) : ExpenseOrPayment
