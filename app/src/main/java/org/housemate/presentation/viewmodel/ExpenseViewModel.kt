package org.housemate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.housemate.domain.model.Expense
import org.housemate.domain.model.ExpenseOrPayment
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

    private val _expenseItems = MutableStateFlow<List<Expense>>(emptyList())
    val expenseItems: StateFlow<List<Expense>> = _expenseItems

    private val _isExpenseHistoryLoading = MutableStateFlow(false)
    val isExpenseHistoryLoading: StateFlow<Boolean> = _isExpenseHistoryLoading

    private val _totalAmountOwedToYou = MutableStateFlow(BigDecimal.ZERO)
    val totalAmountOwedToYou: StateFlow<BigDecimal> = _totalAmountOwedToYou

    private val _totalAmountYouOwe = MutableStateFlow(BigDecimal.ZERO)
    val totalAmountYouOwe: StateFlow<BigDecimal> = _totalAmountYouOwe

    // Total amounts you owe to each housemate
    private val _netAmountOwed = MutableStateFlow<Map<String, BigDecimal>>(emptyMap())
    val netAmountOwed: StateFlow<Map<String, BigDecimal>> = _netAmountOwed

    private val _expenseAndPaymentItems = MutableStateFlow<List<ExpenseOrPayment>>(emptyList())
    val expenseAndPaymentItems: StateFlow<List<ExpenseOrPayment>> = _expenseAndPaymentItems

    init {
        // Fetch expenses from the repository when the ViewModel is initialized
        fetchExpenses()
        fetchPayments()
    }

    private val _dialogDismissed = MutableStateFlow(false)
    val dialogDismissed: StateFlow<Boolean> = _dialogDismissed
    fun dismissDialog() {
        // Update a state variable to trigger recomposition
        _dialogDismissed.value = true
    }

    fun resetDialogDismissed() {
        _dialogDismissed.value = false
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            try {
                _isExpenseHistoryLoading.value = true
                val fetchedExpenses = expenseRepository.getExpenses()
                _expenseItems.value = fetchedExpenses
                println("Fetched expenses: $fetchedExpenses") // Print statement added

                val expenseOrPayments = fetchedExpenses.map { it }

                updateExpenseAndPaymentItems(expenseOrPayments)
                calculateTotalAmounts()
                println("Fetched successfully")

            } catch (e: Exception) {
                // Handle the exception
                println("Failed to fetch expenses: ${e.message}")
            } finally {
                _isExpenseHistoryLoading.value = false
            }
        }
    }

    fun deleteExpenseById(expenseId: String) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpenseById(expenseId)
                clearExpenseAndPaymentItems()
                fetchExpenses()
                fetchPayments()
                println("Expense deleted successfully")
            } catch (e: Exception) {
                println("Failed to delete expense: ${e.message}")
            }
        }
    }

    private fun calculateTotalAmounts() {
        val totalOwedToYou: BigDecimal?
        val totalYouOwe: BigDecimal?
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

        // Calculate amounts from payments
        _paymentItems.value.forEach { payment ->
            val payer = payment.payerName
            val amountPaid = BigDecimal.valueOf(payment.amount)

            // If the payer is "You", you received the payment, else you made the payment
            if (payer == "You") {
                netAmountOwed[payment.payeeName] = (netAmountOwed[payment.payeeName] ?: BigDecimal.ZERO) + amountPaid
            } else {
                netAmountOwed[payer] = (netAmountOwed[payer] ?: BigDecimal.ZERO) - amountPaid
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

    fun addExpense(id: String, payer: String, description: String, expenseAmount: BigDecimal, owingAmounts: Map<String, BigDecimal>) {
        // Convert BigDecimal values to Double, since firestore doesn't accept bigdecimal
        val expenseAmountDouble = expenseAmount.toDouble()
        val owingAmountsDouble = owingAmounts.mapValues { it.value.toDouble() } // Convert each BigDecimal value to Double

        // Create the Expense object with converted values
        val expense = Expense(id, payer, description, expenseAmountDouble, owingAmountsDouble, Timestamp.now())

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

                // Convert fetched payments to ExpenseOrPayment items
                val expenseOrPayments = fetchedPayments.map { it }
                // Update combined list of expenses and payments
                updateExpenseAndPaymentItems(expenseOrPayments)
                calculateTotalAmounts()

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
                val payment = Payment(payerName, payerId, payeeName, payeeId, paymentAmountDouble, Timestamp.now())
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

    private fun clearExpenseAndPaymentItems() {
        _expenseAndPaymentItems.value = emptyList()
    }

    // Combine and sort expenses and payments by timestamp, then update StateFlow
    private fun updateExpenseAndPaymentItems(newItems: List<ExpenseOrPayment>) {
        val currentItems = _expenseAndPaymentItems.value.toMutableList()
        // Filter out any new items that are already present in the current list
        val filteredNewItems = newItems.filter { newItem ->
            currentItems.none { it == newItem }
        }
        // Combine the current list with the filtered new items and sort by timestamp
        val combinedList = (currentItems + filteredNewItems).sortedByDescending { it.timestamp }

        _expenseAndPaymentItems.value = combinedList
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