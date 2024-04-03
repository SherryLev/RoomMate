package org.housemate.domain.repositories

import org.housemate.domain.model.Expense
import org.housemate.domain.model.Payment

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun getExpenses(): List<Expense>
    suspend fun getPayments(): List<Payment>
    suspend fun addPayment(payment: Payment)
}