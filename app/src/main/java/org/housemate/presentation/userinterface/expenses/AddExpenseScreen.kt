package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.theme.green
import org.housemate.theme.light_gray
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_dark_error
import org.housemate.theme.md_theme_light_primary
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun AddExpenseScreen(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val selectedPayer by expenseViewModel.selectedPayer.collectAsState()
    val expenseDescription by expenseViewModel.expenseDescription.collectAsState()
    val expenseAmount by expenseViewModel.expenseAmount.collectAsState()
    val owingAmounts by expenseViewModel.owingAmounts.collectAsState()

    var selectedSplit by remember { mutableStateOf("Equally") }

    val housemates = listOf("You", "Sally", "Bob", "Mike")
    val split_options = listOf("Equally", "By exact amount")

    var expenseAmountState by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }

    var remainingAmountState = remember { mutableStateOf(BigDecimal.ZERO.setScale(2)) }


    var splitUi by remember {
        mutableStateOf<@Composable () -> Unit>(
            { EmptyComposable() }
        )
    }

    // AlertDialog for displaying error message
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
                    "Please fill in all fields.",
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

    val showIncorrectAmountErrorDialog = remember { mutableStateOf(false) }
    if (showIncorrectAmountErrorDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog
                showIncorrectAmountErrorDialog.value = false
            },
            title = { Text("Error") },
            text = {
                Text(
                    "Oops! The amounts do not add up to the total. Please double-check the amounts for each housemate.",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Dismiss the dialog
                        showIncorrectAmountErrorDialog.value = false
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
        .padding(horizontal = 16.dp)
    ) {
        item {
            // Raised surface with currency selection and dollar amount text field
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
                                expenseAmountState.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
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

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    "For",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                OutlinedTextField(
                    value = expenseDescription,
                    onValueChange = { expenseViewModel.setExpenseDescription(it) },
                    singleLine = true,
                    label = { Text("Enter expense description") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .width(240.dp)
                )
            }

            // Dropdown for selecting who paid for the expense
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
            ) {
                Text("By", fontWeight = FontWeight.Bold, color = Color.Gray)

                Spacer(modifier = Modifier.weight(1f))

                CustomDropdown(
                    items = housemates,
                    selectedItem = selectedPayer,
                    onItemSelected = { expenseViewModel.setSelectedPayer(it) },
                    modifier = Modifier,
                    dropdownWidth = 220.dp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
            ) {
                Text("Split", fontWeight = FontWeight.Bold, color = Color.Gray)

                Spacer(modifier = Modifier.weight(1f))

                CustomDropdown(
                    items = split_options,
                    selectedItem = selectedSplit,
                    onItemSelected = { selectedSplit = it },
                    modifier = Modifier,
                    dropdownWidth = 220.dp
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                // Update splitUi based on selected split option
                when (selectedSplit) {
                    "Equally" -> {
                        splitUi = {
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    "With",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 26.dp)
                                )
                                Spacer(modifier = Modifier.width(50.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                EquallySplitUI(
                                    housemates = housemates,
                                    expenseAmountState = expenseAmountState, // Pass textFieldValueState
                                    onAmountChanged = { owingAmounts ->
                                        // Handle the amount change here, you can print or perform any other action
                                        println("Owing Amounts: $owingAmounts")
                                        expenseViewModel.setOwingAmounts(
                                            owingAmounts
                                        )
                                    }
                                )
                            }
                        }
                    }

                    "By exact amount" -> {
                        splitUi = {
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    "With",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 26.dp)
                                )
                                Spacer(modifier = Modifier.width(50.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                ExactAmountSplitUI(
                                    housemates = housemates,
                                    expenseAmountState = expenseAmountState,
                                    remainingAmountState = remainingAmountState,
                                    onAmountChanged = { owingAmounts ->
                                        println("Owing Amounts: $owingAmounts")
                                        expenseViewModel.setOwingAmounts(
                                            owingAmounts
                                        )
                                    }
                                )
                            }
                        }
                    }
                    // Add other cases as needed
                }
                splitUi()
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
                        expenseViewModel.setSelectedPayer("You")
                        expenseViewModel.setExpenseDescription("")
                        expenseViewModel.setExpenseAmount(BigDecimal.ZERO)
                        expenseViewModel.setOwingAmounts(emptyMap())

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
                        if (expenseDescription.isEmpty() || expenseAmount == BigDecimal.ZERO) {
                            // Show the error dialog
                            showEmptyFieldsErrorDialog.value = true
                        } else if (selectedSplit == "By exact amount" && remainingAmountState.value != BigDecimal.ZERO.setScale(2)) {
                            println("remaining: ${remainingAmountState.value}")
                            showIncorrectAmountErrorDialog.value = true
                        } else {

                            // also need:
                            // totalYouOwe to each housemate
                            // totalOwed by each housemate
                            // totalYouOwe to everyone
                            // totalOwed by everyone
                            // this can all be calculated using the expense history, in the viewmodel

                            // for each expense
                            // date of expense

                            // when you click settle up, you should be able to
                            // see how much you owe that person or how much they owe you
                            // you can write a smaller amount
                            // then this should appear in the expense history as a payment
                            expenseViewModel.addExpense(
                                selectedPayer,
                                expenseDescription,
                                expenseAmount,
                                owingAmounts
                            )
                            // clear the form fields after saving the expense
                            expenseViewModel.setSelectedPayer("You")
                            expenseViewModel.setExpenseDescription("")
                            expenseViewModel.setExpenseAmount(BigDecimal.ZERO)
                            expenseViewModel.setOwingAmounts(emptyMap())

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

@Composable
fun EquallySplitUI(
    housemates: List<String>,
    expenseAmountState: TextFieldValue, // Add textFieldValueState parameter
    onAmountChanged: (Map<String, BigDecimal>) -> Unit
) {
    val amountPerPerson = remember(expenseAmountState) { mutableStateOf(BigDecimal.ZERO) } // observe textFieldValueState
    // Map to hold the checked states for each housemate
    val checkedStates = remember { mutableStateListOf<Boolean>().apply { repeat(housemates.size) { add(true) } } }
    // Keep track of the number of selected housemates
    var selectedCount by remember { mutableStateOf(housemates.size) }


    // Calculate amounts whenever textFieldValueState changes
    DisposableEffect(expenseAmountState) {
        amountPerPerson.value = if (selectedCount > 0) {
            expenseAmountState.text.toBigDecimalOrNull()?.let { totalAmount ->
                totalAmount.divide(BigDecimal(selectedCount), 2, RoundingMode.HALF_UP)
            } ?: BigDecimal.ZERO
        } else {
            BigDecimal.ZERO
        }
        // Calculate and return owing amounts
        val owingAmounts = mutableMapOf<String, BigDecimal>()
        housemates.forEachIndexed { i, name ->
            if (checkedStates.getOrNull(i) == true) {
                owingAmounts[name] = amountPerPerson.value
            }
        }
        onAmountChanged(owingAmounts)

        onDispose { }
    }

    // Remember the calculated amount per person
    Column(
        modifier = Modifier.padding(vertical = 12.dp),
    ) {
        // List of housemates with checkboxes
        housemates.forEachIndexed { index, housemate ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = housemate)
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(
                    checked = checkedStates.getOrNull(index) ?: false,
                    onCheckedChange = { checked ->
                        checkedStates[index] = checked
                        if (checked) {
                            selectedCount++
                        } else {
                            selectedCount--
                        }
                        // Recalculate amount per person
                        amountPerPerson.value = if (selectedCount > 0) {
                            expenseAmountState.text.toBigDecimalOrNull()?.let { totalAmount ->
                                totalAmount.divide(BigDecimal(selectedCount), 2, RoundingMode.HALF_UP)
                            } ?: BigDecimal.ZERO
                        } else {
                            BigDecimal.ZERO
                        }
                        // Calculate and return owing amounts
                        val owingAmounts = mutableMapOf<String, BigDecimal>()
                        housemates.forEachIndexed { i, name ->
                            if (checkedStates.getOrNull(i) == true) {
                                owingAmounts[name] = amountPerPerson.value
                            }
                        }

                        var totalOwingAmounts = BigDecimal.ZERO
                        for (value in owingAmounts.values) {
                            totalOwingAmounts = totalOwingAmounts.add(value)
                        }

                        val totalAmount = expenseAmountState.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        if (totalOwingAmounts > totalAmount) {
                            // If total owing amounts exceed the total amount, subtract the difference from the last person
                            val difference = totalOwingAmounts - totalAmount
                            val lastPerson = owingAmounts.keys.last()
                            owingAmounts[lastPerson] = owingAmounts[lastPerson]!! - difference
                        } else if (totalOwingAmounts < totalAmount) {
                            // If total owing amounts are less than the total amount, add the difference to the last person
                            val difference = totalAmount - totalOwingAmounts
                            val lastPerson = owingAmounts.keys.last()
                            owingAmounts[lastPerson] = owingAmounts[lastPerson]!! + difference
                        }

                        onAmountChanged(owingAmounts)
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = md_theme_light_primary,
                        uncheckedColor = Color.Gray
                    )
                )

            }
        }
        Box(modifier = Modifier.height(112.dp)) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 26.dp)
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                // Calculation of how much each person owes
                val text = AnnotatedString.Builder().apply {
                    withStyle(style = SpanStyle(color = md_theme_light_primary, fontWeight = FontWeight.Bold)) {
                        append("$${amountPerPerson.value}")
                    }
                    withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Bold)) {
                        append("/person\n")
                    }
                    withStyle(style = SpanStyle(color = md_theme_light_primary, fontWeight = FontWeight.Bold)) {
                        append("$selectedCount")
                    }
                    withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Bold)) {
                        append(" people")
                    }

                }.toAnnotatedString()

                Text(
                    text = text,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}




@Composable
fun ExactAmountSplitUI(
    housemates: List<String>,
    expenseAmountState: TextFieldValue,
    remainingAmountState: MutableState<BigDecimal>,
    onAmountChanged: (Map<String, BigDecimal>) -> Unit
) {
    // Map to hold the entered amounts for each housemate
    val enteredAmounts = remember { mutableStateMapOf<String, BigDecimal>() }

    // Map to hold the TextFieldValue state for each housemate
    val textFieldValueStates = remember {
        mutableStateMapOf<String, TextFieldValue>().apply {
            housemates.forEach { housemate ->
                this[housemate] = TextFieldValue(text = "")
            }
        }
    }

    // Recalculate entered amounts whenever the total amount changes
    DisposableEffect(expenseAmountState) {
        val sumOfEnteredAmounts = enteredAmounts.values.fold(BigDecimal.ZERO) { acc, value ->
            acc + value
        }
        remainingAmountState.value = expenseAmountState.text.toBigDecimalOrNull()?.minus(sumOfEnteredAmounts) ?: BigDecimal.ZERO.setScale(2)

        onAmountChanged(enteredAmounts.toMap())

        onDispose { }
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        // List of housemates with numerical textfields
        housemates.forEach { housemate ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(50.dp)
            ) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = housemate)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(vertical = 0.dp) // Adjust the vertical spacing here
                ) {
                    TextField(
                        value = textFieldValueStates[housemate] ?: TextFieldValue(""),
                        onValueChange = { newValue ->
                            val formattedValue = formatAmount(newValue)
                            textFieldValueStates[housemate] = formatAmount(newValue)
                            val newAmount =
                                formattedValue.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
                            enteredAmounts[housemate] = newAmount
                            onAmountChanged(enteredAmounts.toMap())
                        },
                        placeholder = {
                            Text(
                                text = "0.00",
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
                            fontSize = 18.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .width(100.dp)
                    )
                }
            }
        }

        // Calculate sum of entered amounts
        val sumOfEnteredAmounts = enteredAmounts.values.fold(BigDecimal.ZERO) { acc, value ->
            acc + value
        }

        remainingAmountState.value = expenseAmountState.text.toBigDecimalOrNull()?.minus(sumOfEnteredAmounts) ?: BigDecimal.ZERO.setScale(2)

        Box(modifier = Modifier.height(112.dp)) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                // Calculation of how much each person owes
                val remainingAmount = remainingAmountState.value
                val text = AnnotatedString.Builder().apply {
                    val remainingText = when {
                        remainingAmount > BigDecimal.ZERO -> {
                            withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Bold)) {
                                append("Remaining amount: ")
                            }
                            withStyle(style = SpanStyle(color = md_theme_light_primary, fontWeight = FontWeight.Bold)) {
                                append("$${remainingAmount.setScale(2)}")
                            }
                        }
                        remainingAmount == (BigDecimal.ZERO.setScale(2)) -> {
                            withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Bold)) {
                                append("Remaining amount: ")
                            }
                            withStyle(style = SpanStyle(color = green, fontWeight = FontWeight.Bold)) {
                                append("$${remainingAmount.setScale(2)}")
                            }
                        }
                        remainingAmount < BigDecimal.ZERO -> {
                            withStyle(style = SpanStyle(color = md_theme_dark_error, fontWeight = FontWeight.Bold)) {
                                append("Oops! The amounts add up to more than the total.")
                            }
                        }
                        else -> {
                            withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Bold)) {
                                append("Remaining amount: ")
                            }
                        }
                    }
                }.toAnnotatedString()

                Text(
                    text = text,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun CustomDropdown(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp? = null // Optional width parameter
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = if (dropdownWidth != null) {
            Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(color = light_gray)
                .clickable { expanded = !expanded }
                .padding(8.dp)
                .width(dropdownWidth)
        } else {
            Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(color = light_gray)
                .clickable { expanded = !expanded }
                .padding(8.dp)
        }

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = selectedItem,
                color = md_theme_light_primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = md_theme_light_primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(0.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = if (dropdownWidth != null) {
                Modifier
                    .width(dropdownWidth + 10.dp)
            } else {
                Modifier
            }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = item,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
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

@Composable
fun EmptyComposable() {
    Box(modifier = Modifier)
}