package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import org.housemate.domain.model.Expense
import org.housemate.domain.model.Payment
import org.housemate.domain.model.User
import org.housemate.domain.repositories.UserRepository
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.green
import org.housemate.theme.light_purple
import org.housemate.theme.light_red
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.md_theme_light_primary
import org.housemate.utils.AppScreenRoutes
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun ExpensesScreen(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses by expenseViewModel.expenseItems.collectAsState()
    val isExpenseHistoryLoading by expenseViewModel.isExpenseHistoryLoading.collectAsState()

    val totalAmountOwedToYou by expenseViewModel.totalAmountOwedToYou.collectAsState()
    val totalAmountYouOwe by expenseViewModel.totalAmountYouOwe.collectAsState()
    val netAmountOwed by expenseViewModel.netAmountOwed.collectAsState()

    val payments by expenseViewModel.paymentItems.collectAsState()
    val expenseAndPaymentItems by expenseViewModel.expenseAndPaymentItems.collectAsState()

    val dialogDismissed by expenseViewModel.dialogDismissed.collectAsState()

    val currentUser by expenseViewModel.currentUser.collectAsState()
    val housemates by expenseViewModel.housemates.collectAsState()

    // Observe dialogDismissed and trigger recomposition
    if (dialogDismissed) {
        expenseViewModel.resetDialogDismissed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = 4.dp,
                        modifier = Modifier
                            .padding(8.dp)
                            .width((LocalConfiguration.current.screenWidthDp * 0.80).dp)
                            .align(Alignment.Center),
                        backgroundColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "You are owed",
                                    fontWeight = FontWeight.Bold, color = Color.DarkGray
                                )
                                // instead of hard coded values, use calculated amounts from viewmodel
                                Text("$${"%.2f".format(totalAmountOwedToYou)}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = green)
                            }
                            Column(
                                modifier = Modifier
                            ) {
                                Divider(
                                    color = Color.LightGray,
                                    modifier = Modifier
                                        .height(45.dp)
                                        .width(1.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "You owe",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                // instead of hard coded values, use calculated amounts from viewmodel
                                Text("$${"%.2f".format(totalAmountYouOwe)}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (totalAmountYouOwe != BigDecimal.ZERO) md_theme_light_error else green)
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        netAmountOwed.forEach { (userId, amount) ->
                            // Find the user object with the corresponding userId
                            val housemate = housemates.find { it.uid == userId }
                            // Get the username from the user object, or use a default value if not found
                            val username = housemate?.username ?: ""
                            val id = housemate?.uid ?: ""
                            // Determine if the user owes or is owed money
                            val youOwe = amount < BigDecimal.ZERO

                            // Call BalancesInfoRow with the username and other parameters
                            BalancesInfoRow(
                                name = username,
                                id = id,
                                amount = "$${"%.2f".format(amount.abs())}",
                                youOwe = youOwe,
                                navController,
                                expenseViewModel
                            )
                        }
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column (
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Expense History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (isExpenseHistoryLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(bottom = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            if (expenses.isEmpty() && payments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(bottom = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "You have no expense history yet",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    expenseAndPaymentItems.forEach { item ->
                                        when (item) {
                                            is Expense -> {
                                                val timestamp =
                                                    item.timestamp.toDate() // Convert Firestore timestamp to Date

                                                val dateFormatter = SimpleDateFormat(
                                                    "MMM",
                                                    Locale.getDefault()
                                                ) // Format for month (e.g., "Dec")
                                                val month =
                                                    dateFormatter.format(timestamp)

                                                val dayOfMonth = SimpleDateFormat(
                                                    "dd",
                                                    Locale.getDefault()
                                                ).format(timestamp)

                                                val amountLentOrBorrowed: Double =
                                                    if (item.payerId == (currentUser?.uid ?: ""))
                                                     {
                                                        // Calculate sum of what others owe you
                                                        item.owingAmounts.values.sum() - (item.owingAmounts[currentUser!!.uid]
                                                            ?: 0.00)
                                                    } else {
                                                        // Get the amount that you owe
                                                        -(item.owingAmounts[currentUser!!.uid] ?: 0.00)
                                                    }

                                                val showDialog = remember { mutableStateOf(false) }
                                                if (showDialog.value) {
                                                    ExpensePopupDialog(
                                                        expense = item,
                                                        onEditExpense = {
                                                            expenseViewModel.onEditExpenseClicked(item)
                                                            navController.navigate(AppScreenRoutes.AddExpenseScreen.route)
                                                                        },
                                                        onDeleteExpense = { expenseViewModel.deleteExpenseById(item.id) },

                                                        onDismiss = {
                                                            showDialog.value = false
                                                            expenseViewModel.dismissDialog()
                                                        },
                                                        housemates = housemates// Dismiss the dialog when needed
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                        .clickable {
                                                            // Show the popup dialog when an expense item is clicked
                                                            showDialog.value = true
                                                        }
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.Start,
                                                        modifier = Modifier.weight(2f)
                                                    ) {
                                                        Text(
                                                            text = month,
                                                            color = Color.Gray,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                        Text(
                                                            text = dayOfMonth,
                                                            color = md_theme_light_primary,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Column(
                                                        modifier = Modifier.weight(6f)
                                                    ) {
                                                        Text(
                                                            text = item.description
                                                        )
                                                        Text(
                                                            text = "${item.payerName} paid $${
                                                                "%.2f".format(
                                                                    item.amount
                                                                )
                                                            }",
                                                            textAlign = TextAlign.Left,
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    val lentOrBorrowedText = when {
                                                        amountLentOrBorrowed < 0 -> "you borrowed"
                                                        amountLentOrBorrowed > 0 -> "you lent"
                                                        else -> "not involved"
                                                    }

                                                    val lentOrBorrowedColor = when {
                                                        amountLentOrBorrowed < 0 -> md_theme_light_error
                                                        amountLentOrBorrowed > 0 -> green
                                                        else -> Color.Gray
                                                    }

                                                    Column(
                                                        modifier = Modifier.weight(3f),
                                                        horizontalAlignment = Alignment.End
                                                    ) {
                                                        Text(
                                                            text = lentOrBorrowedText,
                                                            fontSize = 14.sp,
                                                            textAlign = TextAlign.End,
                                                            color = lentOrBorrowedColor,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "$${
                                                                "%.2f".format(
                                                                    abs(
                                                                        amountLentOrBorrowed
                                                                    )
                                                                )
                                                            }",
                                                            textAlign = TextAlign.End,
                                                            color = lentOrBorrowedColor
                                                        )
                                                    }
                                                }
                                            }
                                            is Payment -> {
                                                val timestamp =
                                                    item.timestamp.toDate() // Convert Firestore timestamp to Date

                                                val dateFormatter = SimpleDateFormat(
                                                    "MMM",
                                                    Locale.getDefault()
                                                ) // Format for month (e.g., "Dec")
                                                val month =
                                                    dateFormatter.format(timestamp)

                                                val dayOfMonth = SimpleDateFormat(
                                                    "dd",
                                                    Locale.getDefault()
                                                ).format(timestamp)

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(
                                                        vertical = 8.dp
                                                    )
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.Start,
                                                        modifier = Modifier.weight(2f)
                                                    ) {
                                                        Text(
                                                            text = month,
                                                            color = Color.Gray,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                        Text(
                                                            text = dayOfMonth,
                                                            color = md_theme_light_primary,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Column(
                                                        modifier = Modifier.weight(6f)
                                                    ) {
                                                        Text(
                                                            text = "${item.payerName} paid ${item.payeeName} $${
                                                                "%.2f".format(
                                                                    item.amount
                                                                )
                                                            }",
                                                            textAlign = TextAlign.Left,
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }


                                                    Column(
                                                        modifier = Modifier.weight(3f),
                                                        horizontalAlignment = Alignment.End
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Paid,
                                                            contentDescription = "Cash",
                                                            tint = green,
                                                            modifier = Modifier
                                                                .size(22.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(0.dp, 15.dp, 0.dp, 0.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(2.dp),
                    onClick = { navController.navigate(AppScreenRoutes.AddExpenseScreen.route) },
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text(
                        "+ Add Expense",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ExpensePopupDialog(
    expense: Expense,
    onEditExpense: () -> Unit,
    onDeleteExpense: () -> Unit,
    onDismiss: () -> Unit,
    housemates: List<User>
)  {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(expense.timestamp.toDate())

    Box(
        modifier = Modifier
            .width(260.dp)
            .padding(horizontal = 20.dp)
    ) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Expense Details                             ")
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = { onEditExpense() },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = light_purple,
                            contentColor = md_theme_light_primary
                        ),
                        elevation = ButtonDefaults.elevation(0.dp),
                    ) {
                        Text(text = "Edit Expense")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { onDeleteExpense() },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = light_red,
                            contentColor = md_theme_light_error
                        ),
                        elevation = ButtonDefaults.elevation(0.dp),
                    ) {
                        Text(text = "Delete Expense")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            },
            text = {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Payer: ${expense.payerName}")
                    Text(text = "Description: ${expense.description}")
                    Text(text = "Amount: $${"%.2f".format(expense.amount)}")
                    Text(text = "Date: $formattedDate")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Owing Amounts:")
                    expense.owingAmounts.forEach { (personId, amount) ->
                        val username = housemates.find { it.uid == personId }?.username ?: ""
                        Text(text = "- $username: $${"%.2f".format(amount)}")
                    }
                }
            }
        )
    }
}

@Composable
fun BalancesInfoRow(name: String, id: String, amount: String, youOwe: Boolean, navController: NavController, expenseViewModel: ExpenseViewModel) {
    Box(
        modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
    ) {
        val amountValue = amount.removePrefix("$").toDoubleOrNull() ?: 0.00
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(90.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textColour = if (youOwe) {
                    md_theme_light_error
                } else {
                    green
                }

                val text = if (youOwe || amountValue == 0.00) {
                    "you owe"
                } else {
                    "owes you"
                }
                Text(text = text, color = textColour, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (youOwe) md_theme_light_error else green
                )
            }

            Button(
                onClick = {
                    expenseViewModel.onSettleUpClicked(name, id, "%.2f".format(amountValue), youOwe)
                    navController.navigate(AppScreenRoutes.SettleUpScreen.route)
                          },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = light_purple,
                    contentColor = md_theme_light_primary
                    ),
                elevation = ButtonDefaults.elevation(0.dp),
                enabled = (amountValue != 0.00)
            ) {
                Text(text = "Settle up", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
