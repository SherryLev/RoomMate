package org.housemate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.housemate.domain.model.Expense
import org.housemate.domain.model.Payment
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

    private val _totalAmountOwedToYou = MutableStateFlow(BigDecimal.ZERO)
    val totalAmountOwedToYou: StateFlow<BigDecimal> = _totalAmountOwedToYou

    private val _totalAmountYouOwe = MutableStateFlow(BigDecimal.ZERO)
    val totalAmountYouOwe: StateFlow<BigDecimal> = _totalAmountYouOwe

    // Total amounts you owe to each housemate
    private val _netAmountOwed = MutableStateFlow<Map<String, BigDecimal>>(emptyMap())
    val netAmountOwed: StateFlow<Map<String, BigDecimal>> = _netAmountOwed

    init {
        // Fetch expenses from the repository when the ViewModel is initialized
        fetchExpenses()
        fetchPayments()
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            try {
                // Call the getExpenses function from the repository to fetch expenses
                _isExpenseHistoryLoading.value = true
                val fetchedExpenses = expenseRepository.getExpenses()
                // Update the _expenses StateFlow object with the fetched expenses
                _expenseItems.value = fetchedExpenses

                calculateTotalAmounts()
            } catch (e: Exception) {
                // Handle the exception
                println("Failed to fetch expenses: ${e.message}")
            } finally {
                _isExpenseHistoryLoading.value = false
            }
        }
    }

    private fun calculateTotalAmounts() {
        var totalOwedToYou = BigDecimal.ZERO
        var totalYouOwe = BigDecimal.ZERO
        val netAmountOwed = mutableMapOf<String, BigDecimal>()

        _expenseItems.value.forEach { expense ->
            // Convert all values in owingAmounts to BigDecimal
            val payer = expense.payer
            val owingAmountsBigDecimal = expense.owingAmounts.mapValues { (_, value) -> BigDecimal.valueOf(value) }

            val youOweAmount = owingAmountsBigDecimal["You"] ?: BigDecimal.ZERO
            // if you didn't pay, then you subtract from the netamountowed
            if (payer != "You") {
                netAmountOwed[payer] = (netAmountOwed[payer] ?: BigDecimal.ZERO) + youOweAmount.negate()
            } else {
                // if you paid, then you add to the netamountowed
                owingAmountsBigDecimal.forEach { (housemate, amount) ->
                    if (housemate != "You") {
                        netAmountOwed[housemate] = (netAmountOwed[housemate] ?: BigDecimal.ZERO) + amount
                    }
                }
            }
        }

        // Calculate total amounts owed to you and by you using netAmountOwed
        totalOwedToYou = netAmountOwed.filterValues { it > BigDecimal.ZERO }.values.sumOf { it }
        totalYouOwe = netAmountOwed.filterValues { it < BigDecimal.ZERO }.values.sumOf { it.abs() }

        // Update StateFlow values with the calculated totals
        _totalAmountOwedToYou.value = totalOwedToYou
        _totalAmountYouOwe.value = totalYouOwe
        _netAmountOwed.value = netAmountOwed
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


    // Define a StateFlow or LiveData object to hold the list of payments
    private val _paymentItems = MutableStateFlow<List<Payment>>(emptyList())
    val paymentItems: StateFlow<List<Payment>> = _paymentItems

    private val _paymentAmount = MutableStateFlow(BigDecimal.ZERO)
    val paymentAmount = _paymentAmount.asStateFlow()

    // Fetch payments from the repository
    private fun fetchPayments() {
        viewModelScope.launch {
            try {
                // Call the getPayments function from the repository to fetch payments
                val fetchedPayments = expenseRepository.getPayments()
                // Update the _paymentItems StateFlow object with the fetched payments
                _paymentItems.value = fetchedPayments
            } catch (e: Exception) {
                // Handle the exception
                println("Failed to fetch payments: ${e.message}")
            }
        }
    }

    // Function to add a payment
    fun addPayment(payerId: String, payerName: String, payeeId: String, payeeName: String, amount: BigDecimal) {
        viewModelScope.launch {
            try {
                val paymentAmountDouble = amount.toDouble()
                // Create the Payment object
                val payment = Payment(payerId, payerName, payeeId, payeeName, paymentAmountDouble, Timestamp.now())
                // Call the addPayment function from the repository to add the payment to Firestore
                expenseRepository.addPayment(payment)
                // After adding the payment, fetch the updated list of payments
                fetchPayments()
                println("Payment added successfully")
            } catch (e: Exception) {
                println("Failed to add payment: ${e.message}")
            }
        }
    }

    private val _selectedHousemate = MutableStateFlow("")
    val selectedHousemate: StateFlow<String> = _selectedHousemate

    private val _selectedOwingAmount = MutableStateFlow("")
    val selectedOwingAmount: StateFlow<String> = _selectedOwingAmount

    private val _selectedOweStatus = MutableStateFlow(false)
    val selectedOweStatus: StateFlow<Boolean> = _selectedOweStatus

    fun onSettleUpClicked(name: String, amount: String, youOwe: Boolean) {
        _selectedHousemate.value = name
        _selectedOwingAmount.value = amount
        _selectedOweStatus.value = youOwe
        setPaymentAmount(amount.toBigDecimalOrNull() ?: BigDecimal.ZERO)
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
    fun setPaymentAmount(amount: BigDecimal) {
        _paymentAmount.value = amount
    }
}