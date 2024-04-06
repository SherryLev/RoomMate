package org.housemate.presentation.userinterface.expenses

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
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
import org.housemate.theme.purple_primary
import java.math.BigDecimal

@Composable
fun SettleUpScreen(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {

    val paymentAmount by expenseViewModel.paymentAmount.collectAsState()
    val selectedHousemate by expenseViewModel.selectedHousemate.collectAsState()
    val selectedOwingAmount by expenseViewModel.selectedOwingAmount.collectAsState()
    val selectedOweStatus by expenseViewModel.selectedOweStatus.collectAsState()
    val selectedHousemateId by expenseViewModel.selectedHousemateId.collectAsState()

    val paymentPayer by expenseViewModel.paymentPayer.collectAsState()
    val paymentPayerId by expenseViewModel.paymentPayerId.collectAsState()
    val paymentPayee by expenseViewModel.paymentPayee.collectAsState()
    val paymentPayeeId by expenseViewModel.paymentPayeeId.collectAsState()

    var paymentAmountState by remember {
        mutableStateOf(TextFieldValue(text = selectedOwingAmount))
    }
    LaunchedEffect(key1 = "fetchUser") {
        expenseViewModel.fetchCurrentUser()
    }
    val currentUser by expenseViewModel.currentUser.collectAsState()

    val payerName = if (selectedOweStatus) currentUser?.username else selectedHousemate
    val payeeName = if (selectedOweStatus) selectedHousemate else currentUser?.username
    val payeeId = if (selectedOweStatus) currentUser?.uid else selectedHousemateId
    val payerId = if (selectedOweStatus) selectedHousemateId else currentUser?.uid
    val paymentId by expenseViewModel.paymentId.collectAsState()

    val showEmptyFieldsErrorDialog = remember { mutableStateOf(false) }
    if (showEmptyFieldsErrorDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog
                showEmptyFieldsErrorDialog.value = false
            },
            title = { Text("Error") },
            text = {
                Text(
                    "Please fill in an amount larger than 0.00",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Dismiss the dialog
                        showEmptyFieldsErrorDialog.value = false
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text("OK")
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }

    // Start of content
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
//        verticalArrangement = Arrangement.Center
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Record a payment",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray),
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
                )
            }
        }

        item {
            // Raised surface with currency selection and dollar amount text field
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(24.dp))
                        Text("Paid", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.width(24.dp))
                        TextField(
                            value = paymentAmountState,
                            onValueChange = {
                                paymentAmountState = formatAmount(it)
                                expenseViewModel.setPaymentAmount(
                                    paymentAmountState.text.toBigDecimalOrNull()?.setScale(2)
                                        ?: BigDecimal.ZERO
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

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 30.dp)
            ) {
                if (paymentPayer != "") {
                    Text(
                        paymentPayer,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    if (payerName != null) {
                        Text(
                            payerName,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = "To",
                    tint = purple_primary,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                if (paymentPayee != "") {
                    Text(
                        paymentPayee,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    if (payeeName != null) {
                        Text(
                            text = payeeName,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
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
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = light_purple,
                        contentColor = purple_primary
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
                        println(paymentAmount)
                        // Check if the expense description or amount is empty
                        if (paymentAmount == BigDecimal.ZERO.setScale(2)) {
                            // Show the error dialog
                            showEmptyFieldsErrorDialog.value = true
                        } else {
                            if (payerName != null) {
                                if (payeeName != null) {
                                    if (payerId != null) {
                                        if (payeeId != null) {
                                            if (paymentId == "") {
                                                expenseViewModel.addPayment(
                                                    paymentId,
                                                    payerId,
                                                    payerName,
                                                    payeeId,
                                                    payeeName,
                                                    paymentAmount
                                                )
                                            } else {
                                                expenseViewModel.updatePaymentById(
                                                    paymentId,
                                                    paymentPayer,
                                                    paymentPayerId,
                                                    paymentPayee,
                                                    paymentPayeeId,
                                                    paymentAmount
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            // clear the form fields after saving the payment
                            expenseViewModel.setPaymentAmount(BigDecimal.ZERO)

                            navController.popBackStack()
                        }
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
