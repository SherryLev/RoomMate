package org.housemate.domain.model

import java.math.BigDecimal

data class Expense(
    val payer: String,
    val description: String,
    val amount: BigDecimal,
    val owingAmounts: Map<String, BigDecimal>
)