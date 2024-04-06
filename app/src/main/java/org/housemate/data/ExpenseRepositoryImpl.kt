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
                val documentReference = firestore.collection("expenses").add(expense).await()
                val expenseWithId = expense.copy(id = documentReference.id)
                firestore.collection("expenses").document(documentReference.id).set(expenseWithId).await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding expense", e)
            throw e
        }
    }

    override suspend fun getExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                // Retrieve the current user's group code from the users collection
                val userDocument = firestore.collection("users").document(userId).get().await()
                val groupCode = userDocument.getString("groupCode")
                if (groupCode != null) {
                    // Fetch the list of member user IDs from the groups collection
                    val groupDocument = firestore.collection("groups").document(groupCode).get().await()
                    val members = groupDocument.get("members") as? List<*>
                    if (members != null) {
                        // Query expenses where the payerId is within the group and all IDs in owingAmounts map are members of the group
                        val querySnapshot = firestore.collection("expenses")
                            .whereIn("payerId", members)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .get()
                            .await()

                        for (document in querySnapshot.documents) {
                            val expense = document.toObject(Expense::class.java)
                            expense?.let {
                                val payerId = it.payerId
                                val owingAmountsIds = it.owingAmounts.keys.toList()
                                if (members.contains(payerId) && owingAmountsIds.all { memberId -> members.contains(memberId) }) {
                                    expenses.add(it)
                                }
                            }
                        }
                    } else {
                        throw IllegalStateException("Members list not found in group document")
                    }
                } else {
                    throw IllegalStateException("Group code not found for user")
                }
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
            Log.d(TAG, "Expenses retrieved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting expenses", e)
            // You can handle the exception here, e.g., return an empty list or throw it to be handled by the caller
        }
        return expenses
    }



//    override suspend fun getExpenses(): List<Expense> {
//        val expenses = mutableListOf<Expense>()
//        try {
//            val currentUser = auth.currentUser
//            val userId = currentUser?.uid
//            if (userId != null) {
//                // Retrieve the current user's group code from the users collection
//                val userDocument = firestore.collection("users").document(userId).get().await()
//                val groupCode = userDocument.getString("groupCode")
//                if (groupCode != null) {
//                    // Fetch the list of member user IDs from the groups collection
//                    val groupDocument = firestore.collection("groups").document(groupCode).get().await()
//                    val members = groupDocument.get("members") as? List<*>
//                    if (members != null) {
//                        // Query expenses where the payerId is within the group
//                        val querySnapshot = firestore.collection("expenses")
//                            .whereIn("payerId", members)
//                            .orderBy("timestamp", Query.Direction.DESCENDING)
//                            .get()
//                            .await()
//
//                        for (document in querySnapshot.documents) {
//                            val expense = document.toObject(Expense::class.java)
//                            expense?.let {
//                                expenses.add(it)
//                            }
//                        }
//                    } else {
//                        throw IllegalStateException("Members list not found in group document")
//                    }
//                } else {
//                    throw IllegalStateException("Group code not found for user")
//                }
//            } else {
//                throw IllegalStateException("User is not authenticated.")
//            }
//            Log.d(TAG, "Expenses retrieved successfully")
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting expenses", e)
//            // You can handle the exception here, e.g., return an empty list or throw it to be handled by the caller
//        }
//        return expenses
//    }


    override suspend fun updateExpenseById(expenseId: String, updatedExpense: Expense) {
        try {
            // Retrieve the existing timestamp value from Firestore
            val existingExpense = firestore.collection("expenses").document(expenseId).get().await()
            val timestamp = existingExpense.getTimestamp("timestamp") // Adjust this based on your Firestore document structure

            // Update the updated expense with the existing timestamp
            val expenseWithTimestamp = timestamp?.let { updatedExpense.copy(timestamp = it) }

            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                if (expenseWithTimestamp != null) {
                    firestore.collection("expenses").document(expenseId).set(expenseWithTimestamp).await()
                }
                Log.d(TAG, "Expense updated successfully")
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating expense", e)
            throw e
        }
    }

    override suspend fun deleteExpenseById(expenseId: String) {
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                firestore.collection("expenses").document(expenseId).delete().await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting expense", e)
            throw e
        }
    }

    override suspend fun getPayments(): List<Payment> {
        val payments = mutableListOf<Payment>()
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                // Retrieve the current user's group code from the users collection
                val userDocument = firestore.collection("users").document(userId).get().await()
                val groupCode = userDocument.getString("groupCode")
                if (groupCode != null) {
                    // Fetch the list of member user IDs from the groups collection
                    val groupDocument = firestore.collection("groups").document(groupCode).get().await()
                    val members = groupDocument.get("members") as? List<*>
                    if (members != null) {
                        // Query payments where the payerId is within the group
                        val querySnapshot = firestore.collection("payments")
                            .whereIn("payerId", members)
                            .whereIn("payeeId", members)
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
                        throw IllegalStateException("Members list not found in group document")
                    }
                } else {
                    throw IllegalStateException("Group code not found for user")
                }
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
            Log.d(TAG, "Payments retrieved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting payments", e)
            // You can handle the exception here, e.g., return an empty list or throw it to be handled by the caller
        }
        return payments
    }

    override suspend fun addPayment(payment: Payment) {
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                val documentReference = firestore.collection("payments").add(payment).await()
                val paymentWithId = payment.copy(id = documentReference.id)
                firestore.collection("payments").document(documentReference.id).set(paymentWithId).await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding payment", e)
            throw e
        }
    }

    override suspend fun updatePaymentById(paymentId: String, updatedPayment: Payment) {
        try {
            // Retrieve the existing timestamp value from Firestore
            val existingPayment = firestore.collection("payments").document(paymentId).get().await()
            val timestamp = existingPayment.getTimestamp("timestamp") // Adjust this based on your Firestore document structure

            // Update the updated expense with the existing timestamp
            val paymentWithTimestamp = timestamp?.let { updatedPayment.copy(timestamp = it) }

            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                if (paymentWithTimestamp != null) {
                    firestore.collection("payments").document(paymentId).set(paymentWithTimestamp).await()
                }
                Log.d(TAG, "Payment updated successfully")
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating payment", e)
            throw e
        }
    }

    override suspend fun deletePaymentById(paymentId: String) {
        try {
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                firestore.collection("payments").document(paymentId).delete().await()
            } else {
                throw IllegalStateException("User is not authenticated.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting payment", e)
            throw e
        }
    }

}
