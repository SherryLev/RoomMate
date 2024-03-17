package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.theme.light_gray
import org.housemate.theme.light_purple
import org.housemate.theme.md_theme_light_primary

@Composable
fun AddExpenseScreen(navController: NavHostController = rememberNavController()) {
    var selectedPayer by remember { mutableStateOf("You") }
    var selectedSplit by remember { mutableStateOf("Equally") }
    var selectedCurrency by remember { mutableStateOf(currencies[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Raised surface with currency selection and dollar amount text field
        Surface(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Paid")
                Spacer(modifier = Modifier.width(16.dp))
                CurrencyDropdown(selectedCurrency = selectedCurrency) {
                    selectedCurrency = it
                }

            }
        }

        // Textfield for entering expense description
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("For")
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = "",
                onValueChange = { /* handle text change */ },
                label = { Text("Expense description") }
            )
        }

        // Dropdown for selecting who paid for the expense
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("By")
            Spacer(modifier = Modifier.width(16.dp))
            DropdownMenu(
                expanded = false,
                onDismissRequest = { /* handle dismiss */ }
            ) {
                DropdownMenuItem(onClick = { selectedPayer = "You" }) {
                    Text("You")
                }
            }
        }

        // Dropdown for selecting split
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Split")
            Spacer(modifier = Modifier.width(16.dp))
            DropdownMenu(
                expanded = false,
                onDismissRequest = { /* handle dismiss */ }
            ) {
                DropdownMenuItem(onClick = { selectedSplit = "Equally" }) {
                    Text("Equally")
                }
            }
        }

        // Section for selecting people in the group
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("With")
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Checkbox(checked = true, onCheckedChange = { /* handle checkbox state */ })
                Checkbox(checked = true, onCheckedChange = { /* handle checkbox state */ })
            }
        }

        // Row for cancel and save buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { /* handle cancel */ }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { /* handle save */ }) {
                Text("Save")
            }
        }
    }
}
// Define a list of currency codes
val currencies = listOf("CAD", "USD", "EUR", "GBP")
@Composable
fun CurrencyDropdown(selectedCurrency: String, onCurrencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(15.dp)
            .size(width = 90.dp, height = 40.dp)
            .background(color = light_gray, shape = RoundedCornerShape(25.dp))
            .clip(RoundedCornerShape(25.dp)) // Clip the clickable area with rounded corners
            .clickable { expanded = !expanded } // Make the whole dropdown clickable
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = selectedCurrency,
                color = md_theme_light_primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = md_theme_light_primary,
                modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(100.dp)
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currency,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

