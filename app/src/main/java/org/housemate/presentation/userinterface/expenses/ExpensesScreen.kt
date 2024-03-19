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
import org.housemate.theme.md_theme_dark_primary
import org.housemate.theme.md_theme_dark_secondary
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.md_theme_light_primary
import org.housemate.theme.purple_background
import org.housemate.utils.AppScreenRoutes

@Composable
fun ExpensesScreen(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenseItems by expenseViewModel.expenseItems.collectAsState(emptyList())

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
                                Text("$2", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = green)
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
                                Text("$200", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = md_theme_light_error)
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
                        // also need:
                        // totalYouOwe to each housemate
                        // totalOwed by each housemate
                        // totalYouOwe to everyone
                        // totalOwed by everyone
                        // this can all be calculated using the expense history, in the viewmodel

                        BalancesInfoRow(name = "Sally", amount = "$2", youOwe = false)
                        BalancesInfoRow(name = "Bob", amount = "$100", youOwe = true)
                        BalancesInfoRow(name = "Mike", amount = "$100", youOwe = true)
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
                    Column {
                        Text(
                            text = "Expense History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        )

                        if (expenseItems.isEmpty()) {
                            Box( modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(bottom = 40.dp),
                                contentAlignment = Alignment.Center){
                                Text(
                                    text = "You have no expense history yet",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier)
                            }
                        } else {
                            // Observe the expenseItems list from the ViewModel
                            expenseItems.forEach { expenseItem ->
                                Text(
                                    text = expenseItem,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 6.dp, end = 6.dp)
                                )
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
fun BalancesInfoRow(name: String, amount: String, youOwe: Boolean) {
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
                onClick = { /* Handle settle up */ },
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
