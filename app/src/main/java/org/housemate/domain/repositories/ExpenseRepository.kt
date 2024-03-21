package org.housemate.domain.repositories

import org.housemate.domain.model.Expense

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun getExpenses(): List<Expense>
}