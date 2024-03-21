package org.housemate.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.housemate.domain.model.Expense
import org.housemate.domain.repositories.ExpenseRepository
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
): ViewModel() {
    private val _selectedPayer = MutableStateFlow("You")
    val selectedPayer = _selectedPayer.asStateFlow()

    private val _expenseDescription = MutableStateFlow("")
    val expenseDescription = _expenseDescription.asStateFlow()

    private val _expenseAmount = MutableStateFlow(BigDecimal.ZERO)
    val expenseAmount = _expenseAmount.asStateFlow()

    private val _owingAmounts = MutableStateFlow<Map<String, BigDecimal>>(emptyMap())
    val owingAmounts = _owingAmounts.asStateFlow()

    // Define a StateFlow or LiveData object to hold the list of expenses
    private val _expenseItems = MutableStateFlow<List<Expense>>(emptyList())
    val expenseItems: StateFlow<List<Expense>> = _expenseItems

    // Define a StateFlow or LiveData object to hold the loading state
    private val _isExpenseHistoryLoading = MutableStateFlow(false)
    val isExpenseHistoryLoading: StateFlow<Boolean> = _isExpenseHistoryLoading

    init {
        // Fetch expenses from the repository when the ViewModel is initialized
        fetchExpenses()
    }
    private fun fetchExpenses() {
        viewModelScope.launch {
            try {
                // Call the getExpenses function from the repository to fetch expenses
                _isExpenseHistoryLoading.value = true
                val fetchedExpenses = expenseRepository.getExpenses()
                // Update the _expenses StateFlow object with the fetched expenses
                _expenseItems.value = fetchedExpenses
            } catch (e: Exception) {
                // Handle the exception
                println("Failed to fetch expenses: ${e.message}")
            } finally {
                _isExpenseHistoryLoading.value = false
            }
        }
    }

    // Function to add an expense with the selected payer's name
    fun addExpense(payer: String, description: String, expenseAmount: BigDecimal, owingAmounts: Map<String, BigDecimal>) {
        // Convert BigDecimal values to Double, since firestore doesn't accept bigdecimal
        val expenseAmountDouble = expenseAmount.toDouble()
        val owingAmountsDouble = owingAmounts.mapValues { it.value.toDouble() } // Convert each BigDecimal value to Double

        // Create the Expense object with converted values
        val expense = Expense(payer, description, expenseAmountDouble, owingAmountsDouble, Timestamp.now())

        // Call the addExpense function from the repository to add the expense to Firestore
        viewModelScope.launch {
            try {
                expenseRepository.addExpense(expense)
                // After adding the expense, fetch the updated list of expenses
                fetchExpenses()
                println("Expense added successfully")

            } catch (e: Exception) {
                println("Failed to add expense: ${e.message}")
            }
        }
    }

        // Functions to update the state
    fun setSelectedPayer(payer: String) {
        _selectedPayer.value = payer
    }

    fun setExpenseDescription(description: String) {
        _expenseDescription.value = description
    }

    fun setExpenseAmount(amount: BigDecimal) {
        _expenseAmount.value = amount
    }

    fun setOwingAmounts(owingAmounts: Map<String, BigDecimal>) {
        _owingAmounts.value = owingAmounts
    }
}