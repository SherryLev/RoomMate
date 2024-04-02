package org.housemate.presentation.userinterface.expenses

import android.icu.math.BigDecimal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_light_primary

@Composable
fun SettleUpScreen(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    var expenseAmountState by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }
    // Start of content
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            // Raised surface with currency selection and dollar amount text field
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Paid", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.width(26.dp))
                        TextField(
                            value = expenseAmountState,
                            onValueChange = {
                                expenseAmountState = formatAmount(it)
                                expenseViewModel.setExpenseAmount(
                                    expenseAmountState.text.toBigDecimalOrNull()
                                        ?: java.math.BigDecimal.ZERO
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Enter Amount",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 26.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .height(IntrinsicSize.Max)
                                .background(color = Color.Transparent)
                        )
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f),
                    onClick = {
                        // clear the form fields after saving the expense
//                        expenseViewModel.setSelectedPayer("You")

                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = light_purple,
                        contentColor = md_theme_light_primary
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text(
                        "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f),
                    onClick = {
                        // Check if the expense description or amount is empty
//                        if (expenseDescription.isEmpty() || expenseAmount == java.math.BigDecimal.ZERO) {
//                            // Show the error dialog
//                            showEmptyFieldsErrorDialog.value = true
//                        } else if (selectedSplit == "By exact amount" && remainingAmountState.value != java.math.BigDecimal.ZERO.setScale(2)) {
//                            println("remaining: ${remainingAmountState.value}")
//                            showIncorrectAmountErrorDialog.value = true
//                        } else {
//
//                            // also need:
//                            // totalYouOwe to each housemate
//                            // totalOwed by each housemate
//                            // totalYouOwe to everyone
//                            // totalOwed by everyone
//                            // this can all be calculated using the expense history, in the viewmodel
//
//                            // for each expense
//                            // date of expense
//
//                            // when you click settle up, you should be able to
//                            // see how much you owe that person or how much they owe you
//                            // you can write a smaller amount
//                            // then this should appear in the expense history as a payment
//                            expenseViewModel.addExpense(
//                                selectedPayer,
//                                expenseDescription,
//                                expenseAmount,
//                                owingAmounts
//                            )
//                            // clear the form fields after saving the expense
//                            expenseViewModel.setSelectedPayer("You")
//                            expenseViewModel.setExpenseDescription("")
//                            expenseViewModel.setExpenseAmount(java.math.BigDecimal.ZERO)
//                            expenseViewModel.setOwingAmounts(emptyMap())
//
//                            navController.popBackStack()
                    },
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text(
                        "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

            }
        }
    }
}

private fun formatAmount(input: TextFieldValue): TextFieldValue {
    var formattedInput = input.text.replace("[^\\d]".toRegex(), "") // Remove any non-digit characters

    // Insert decimal point based on the length of the input
    when (formattedInput.length) {
        0 -> formattedInput = ""
        1 -> formattedInput = "0.0$formattedInput"
        else -> {
            val value = formattedInput.substring(0, formattedInput.length - 2)
            val cents = formattedInput.substring(formattedInput.length - 2)
            formattedInput = when {
                value.isEmpty() -> "0.$cents"
                value == "0" -> "0.$cents"
                value == "0." -> "0.$cents"
                value.length > 1 && value.startsWith('0')-> "${value.substring(1)}.$cents"
                else -> "$value.$cents"
            }
        }
    }
    return TextFieldValue(text = formattedInput, selection = TextRange(formattedInput.length))
}
