package org.housemate.domain.model

import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExpenseTest {

    lateinit var expense: Expense
    @Before
    fun setUp() {
        expense = Expense(
            id = "expense123",
            payerName = "Sally",
            payerId = "payer456",
            description = "Test expense",
            amount = 50.0,
            owingAmounts = mapOf("Alice" to 25.0, "Bob" to 25.0),
            timestamp = Timestamp(12345, 0)
        )
    }

    @Test
    fun getId() {
        assertEquals("expense123", expense.id)
    }

    @Test
    fun getPayerName() {
        assertEquals("Sally", expense.payerName)
    }

    @Test
    fun getPayerId() {
        assertEquals("payer456", expense.payerId)
    }

    @Test
    fun getDescription() {
        assertEquals("Test expense", expense.description)
    }

    @Test
    fun getAmount() {
        assertEquals(50.0, expense.amount, 0.001)
    }

    @Test
    fun getOwingAmounts() {
        assertEquals(mapOf("Alice" to 25.0, "Bob" to 25.0), expense.owingAmounts)
    }

    @Test
    fun getTimestamp() {
        assertEquals(Timestamp(12345, 0), expense.timestamp)
    }
}