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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.theme.light_gray
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_light_primary
import org.housemate.utils.AppScreenRoutes
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun AddExpenseScreen(navController: NavHostController = rememberNavController()) {
    var selectedPayer by remember { mutableStateOf("You") }
    var selectedSplit by remember { mutableStateOf("Equally") }
    var selectedCurrency by remember { mutableStateOf("CAD") }
    var expenseDescription by remember { mutableStateOf("") }

    val currencies = listOf("CAD", "USD", "EUR", "GBP")
    val housemates = listOf("You", "Sally", "Bob", "Mike")
    val split_options = listOf("Equally", "By exact amount", "By %")

    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }

    var splitUi by remember {
        mutableStateOf<@Composable () -> Unit>(
            { EmptyComposable() }
        )
    }

    Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
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
                Spacer(modifier = Modifier.width(22.dp))
                CustomDropdown(
                    items = currencies,
                    selectedItem = selectedCurrency,
                    onItemSelected = { selectedCurrency = it },
                    modifier = Modifier,
                    dropdownWidth = 78.dp
                )
                Spacer(modifier = Modifier.width(22.dp))
                TextField(
                    value = textFieldValueState,
                    onValueChange = { textFieldValueState = formatAmount(it) },
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
                onValueChange = { expenseDescription = it },
                singleLine = true,
                label = { Text("Enter expense description") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.width(240.dp)
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
                onItemSelected = { selectedPayer = it },
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

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
//        ) {
//            Text("With", fontWeight = FontWeight.Bold, color = Color.Gray)
//            Column(
//                modifier = Modifier.padding(start = 16.dp)
//            ) {
//                Checkbox(checked = true, onCheckedChange = { /* handle checkbox state */ })
//                Checkbox(checked = true, onCheckedChange = { /* handle checkbox state */ })
//            }
//        }

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            // Update splitUi based on selected split option
            when (selectedSplit) {
                "Equally" -> {
                    splitUi = {
                        EquallySplitUI(
                            housemates = housemates,
                            totalAmount = textFieldValueState.text.toBigDecimalOrNull()
                                ?: BigDecimal.ZERO,
                            onAmountChanged = {}
                        )
                    }
                }

                "By exact amount" -> {
                    splitUi = {
                        ExactAmountSplitUI(
                            housemates = housemates,
                            totalAmount = textFieldValueState.text.toBigDecimalOrNull()
                                ?: BigDecimal.ZERO,
                            onAmountChanged = { /* Handle amount change */ }
                        )
                    }
                }
                // Add other cases as needed
            }

            // Existing code...

            // Render the dynamic split UI
            splitUi()

            // Existing code...
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
                    onClick = { },
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
                    onClick = { },
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
    totalAmount: BigDecimal,
    onAmountChanged: (Map<String, BigDecimal>) -> Unit
) {
    // Map to hold the entered amounts for each housemate
    val enteredAmounts = remember { mutableStateMapOf<String, BigDecimal>() }

    // Map to hold the checked states for each housemate
    val checkedStates = remember { mutableStateListOf<Boolean>().apply { repeat(housemates.size) { add(false) } } }

    // Keep track of the number of selected housemates
    var selectedCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // List of housemates with checkboxes
        housemates.forEachIndexed { index, housemate ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedStates.getOrNull(index) ?: false,
                    onCheckedChange = { checked ->
                        checkedStates[index] = checked
                        if (checked) {
                            selectedCount++
                        } else {
                            selectedCount--
                        }
                        // Notify the amount changed when checkbox state changes
                        onAmountChanged(enteredAmounts.toMap())
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = housemate)
            }
        }

        // Calculate amount per person
        val amountPerPerson = if (selectedCount > 0) {
            totalAmount.divide(BigDecimal(selectedCount), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        // Calculation of how much each person owes
        Text(
            text = "Amount per person: $amountPerPerson, # people: $selectedCount",
            modifier = Modifier.padding(top = 8.dp)
        )

        // Print entered amounts map
        Button(
            onClick = {
                println("Entered amounts:")
                enteredAmounts.forEach { (housemate, amount) ->
                    println("$housemate: $amount")
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Print Entered Amounts")
        }
    }
}



@Composable
fun ExactAmountSplitUI(
    housemates: List<String>,
    totalAmount: BigDecimal,
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

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // List of housemates with numerical textfields
        housemates.forEach { housemate ->
            val enteredAmount = enteredAmounts[housemate] ?: BigDecimal.ZERO
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = housemate)
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = textFieldValueStates[housemate] ?: TextFieldValue(""),
                    onValueChange = { newValue ->
                        val formattedValue = formatAmount(newValue)
                        textFieldValueStates[housemate] = formatAmount(newValue)
                        val newAmount = formattedValue.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        enteredAmounts[housemate] = newAmount
                        onAmountChanged(enteredAmounts.toMap())
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
                        fontSize = 18.sp,
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

        // Calculate sum of entered amounts
        val sumOfEnteredAmounts = enteredAmounts.values.fold(BigDecimal.ZERO) { acc, value ->
            acc + value
        }

        // Calculate remaining amount needed to match the total
        val remainingAmount = totalAmount - sumOfEnteredAmounts

        // Display remaining amount
        Text(
            text = "Remaining amount: ${if (remainingAmount >= BigDecimal.ZERO) remainingAmount.setScale(2) else BigDecimal.ZERO}",
            modifier = Modifier.padding(top = 8.dp),
            color = if (remainingAmount >= BigDecimal.ZERO) Color.Green else Color.Red
        )

        // Print entered amounts map
        Button(
            onClick = {
                println("Entered amounts:")
                enteredAmounts.forEach { (housemate, amount) ->
                    println("$housemate: $amount")
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Print Entered Amounts")
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