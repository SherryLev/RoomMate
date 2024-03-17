package org.housemate.presentation.userinterface.chores

import android.text.Selection
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import org.housemate.domain.model.Chore
import java.time.LocalDateTime

var choreIdCount = 1

@Composable
fun SelectionDropdown(options: List<String>, label: String, onCategorySelected: (String) -> Unit){

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = false,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = {Text(label)},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()})
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption = option
                    onCategorySelected(option) // Callback to update the category in the Chore object
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun ChoreCreator(addChore: (Chore) -> Unit,  onDialogDismiss: () -> Unit) {
    val categories = listOf( "Kitchen", "Living Room", "Dining Room", "Staircase", "Backyard")
    val choreList = listOf( "Clean dishes", "Sweep Floors", "Clean Toilet", "Vaccum Floor")
    val assignees = listOf( "Bob", "Marlee", "Shawn", "Ben", "Laura")
    val labels =  listOf("Chore","Category", "Assignee" )
    var categoryChoice by remember { mutableStateOf("") }
    var choreChoice by remember { mutableStateOf("") }
    var assigneeChoice by remember { mutableStateOf("") }
    val id = choreIdCount
    var dueDate = LocalDateTime.now()
    choreIdCount++
    Column(
        modifier = Modifier.padding(16.dp)) {
        SelectionDropdown(choreList,labels[0], onCategorySelected = { category -> choreChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        SelectionDropdown(categories,labels[1], onCategorySelected = { category -> categoryChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        SelectionDropdown(assignees,labels[2],onCategorySelected = { category -> assigneeChoice = category })
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                val chore = Chore(id = id, choreName = choreChoice, category = categoryChoice, assignee = assigneeChoice, dueDate = dueDate)
                addChore(chore)
                dueDate = LocalDateTime.now()
                onDialogDismiss()
            },
            modifier = Modifier
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                //contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "Create",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}