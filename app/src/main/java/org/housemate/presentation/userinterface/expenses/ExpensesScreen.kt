package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.Divider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.green
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_dark_error
import org.housemate.theme.md_theme_dark_primary
import org.housemate.theme.md_theme_dark_secondary
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.md_theme_light_primary
import org.housemate.theme.purple_background
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
    val housematesOweYou by expenseViewModel.housematesOweYou.collectAsState()
    val youOweHousemates by expenseViewModel.youOweHousemates.collectAsState()

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
                                Text("$${totalAmountOwedToYou}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = green)
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
                                Text("$${totalAmountYouOwe}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = md_theme_light_error)
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
                        // instead of hard coded values, use calculated amounts from viewmodel
                        housematesOweYou.forEach { (housemate, amount) ->
                            BalancesInfoRow(name = housemate, amount = "$${amount}", youOwe = false, navController)
                        }
                        youOweHousemates.forEach { (housemate, amount) ->
                            BalancesInfoRow(name = housemate, amount = "$${amount}", youOwe = true, navController)
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
                            if (expenses.isEmpty()) {
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
                                    // Observe the expenseItems list from the ViewModel
                                    expenses.forEach { expense ->
                                        val timestamp =
                                            expense.timestamp.toDate() // Convert Firestore timestamp to Date

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
                                            if (expense.payer == "You") {
                                                // Calculate sum of what others owe you
                                                expense.owingAmounts.values.sum() - (expense.owingAmounts["You"]
                                                    ?: 0.00)
                                            } else {
                                                // Get the amount that you owe
                                                -(expense.owingAmounts["You"] ?: 0.00)
                                            }

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
                                                    text = expense.description
                                                )
                                                Text(
                                                    text = "${expense.payer} paid $${
                                                        "%.2f".format(
                                                            expense.amount
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
fun BalancesInfoRow(name: String, amount: String, youOwe: Boolean, navController: NavController) {
    Box(
        modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (youOwe) {
                    Text(text = "you owe", color = md_theme_light_error, fontSize = 14.sp)
                } else {
                    Text(text = "owes you", color = green, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (youOwe) md_theme_light_error else green
                )
            }
            Button(
                onClick = { navController.navigate(AppScreenRoutes.SettleUpScreen.route) },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = light_purple,
                    contentColor = md_theme_light_primary
                    ),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(text = "Settle up", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
