package org.housemate.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(): ViewModel() {
    private val _selectedPayer = MutableStateFlow("You")
    val selectedPayer = _selectedPayer.asStateFlow()

    private val _expenseDescription = MutableStateFlow("")
    val expenseDescription = _expenseDescription.asStateFlow()

    private val _expenseAmount = MutableStateFlow(BigDecimal.ZERO)
    val expenseAmount = _expenseAmount.asStateFlow()

    private val _owingAmount = MutableStateFlow(BigDecimal.ZERO)
    val owingAmount = _owingAmount.asStateFlow()

    private val _expenseItems = MutableStateFlow<List<String>>(emptyList())
    val expenseItems = _expenseItems.asStateFlow()

    // Function to add an expense with the selected payer's name
    fun addExpense(payer: String, description: String, expenseAmount: BigDecimal, owingAmount: BigDecimal) {
        val newExpense = "$payer paid $expenseAmount for $description\n you owe $owingAmount"
        _expenseItems.value = _expenseItems.value + newExpense
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

    fun setOwingAmount(amount: BigDecimal) {
        _owingAmount.value = amount
    }
}