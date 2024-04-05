package org.housemate.domain.model

import com.google.firebase.Timestamp

data class Payment(
    val id: String,
    val payerName: String = "",
    val payerId: String = "",
    val payeeName: String = "",
    val payeeId: String = "",
    override val amount: Double = 0.0,
    override val timestamp: Timestamp = Timestamp.now()
) : ExpenseOrPayment {
    constructor() : this("", "", "", "", "", 0.0, Timestamp.now())
}