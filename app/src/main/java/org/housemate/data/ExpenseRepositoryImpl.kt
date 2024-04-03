package org.housemate.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.housemate.domain.model.Expense
import org.housemate.domain.repositories.ExpenseRepository
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import org.housemate.domain.model.Payment

class ExpenseRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
    ) : ExpenseRepository {

    companion object {
        private const val TAG = "ExpenseRepository"
    }

    override suspend fun addExpense(expense: Expense) {
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                firestore.collection("expenses").add(expense).await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding expense", e)
            // You can handle the exception here, e.g., throw it to be handled by the caller
            throw e
        }
    }

    override suspend fun getExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                val querySnapshot = firestore.collection("expenses")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val expense = document.toObject(Expense::class.java)
                    expense?.let {
                        expenses.add(it)
                    }
                }
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting expenses", e)
            // You can handle the exception here, e.g., return an empty list or throw it to be handled by the caller
        }
        return expenses
    }


    override suspend fun getPayments(): List<Payment> {
        val payments = mutableListOf<Payment>()
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                val querySnapshot = firestore.collection("payments")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                for (document in querySnapshot.documents) {
                    val payment = document.toObject(Payment::class.java)
                    payment?.let {
                        payments.add(it)
                    }
                }
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting payments", e)
        }
        return payments
    }

    override suspend fun addPayment(payment: Payment) {
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                firestore.collection("payments").add(payment).await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding payment", e)
            // You can handle the exception here, e.g., throw it to be handled by the caller
            throw e
        }
    }
}
