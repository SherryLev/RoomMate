package org.housemate.presentation.userinterface.chores

import android.app.DatePickerDialog
import android.text.Selection
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import org.housemate.domain.model.Chore
import org.housemate.domain.model.User
import java.time.LocalDateTime
import java.util.*
import org.housemate.domain.repositories.ChoreRepository
import org.housemate.domain.repositories.UserRepository
import java.time.temporal.ChronoUnit

var choreIdCount = 1
@Composable
fun ReadonlyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.width(120.dp),
            label = label,

            )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = onClick),
        )
    }
}

@Composable
fun TasksDatePicker(onDateSelected: (LocalDateTime) -> Unit): LocalDateTime? {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // getting today's date fields
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]

    var selectedDateText by remember { mutableStateOf("") }

    val datePicker =
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0))
            },
            year,
            month,
            day,
        )
    // can't pick dates in the past
    datePicker.datePicker.minDate = calendar.timeInMillis

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        ReadonlyOutlinedTextField(
            value = selectedDateText,
            onValueChange = { selectedDateText = it },
            onClick = {
                datePicker.show()
            },
        ) {
            Text(text = "Start Date")
        }
    }
    return null
}

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
fun ChoreCreator(createChore: (Chore) -> Unit,  onDialogDismiss: () -> Unit, choreRepository: ChoreRepository, userRepository: UserRepository){
    val categories = listOf( "Kitchen", "Living Room", "Dining Room", "Staircase", "Backyard")
    val choreList = listOf( "Clean dishes", "Sweep Floors", "Clean Toilet", "Vaccum Floor")
    val assignees = listOf( "Bob", "Marlee", "Shawn", "Ben", "Laura")
    val labels =  listOf("Chore","Category", "Assignee" )
    var categoryChoice by remember { mutableStateOf("") }
    var choreChoice by remember { mutableStateOf("") }
    var assigneeChoice by remember { mutableStateOf("") }

    val selectedDate = remember { mutableStateOf<LocalDateTime?>(null) }
    val repetitionOptions = listOf("None", "Every Day", "Week", "2 Weeks", "3 Weeks", "4 Weeks")
    var repetitionChoice by remember { mutableStateOf("None") }
    var choreCounter by remember { mutableStateOf(0) }
    val choreId = "chore${choreIdCount++}" // Generate unique chore ID

    var userId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = "userId") {
        userId = userRepository.getCurrentUserId()
    }

    Column(
        modifier = Modifier.padding(16.dp)) {
        SelectionDropdown(choreList,labels[0], onCategorySelected = { category -> choreChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        SelectionDropdown(categories,labels[1], onCategorySelected = { category -> categoryChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        SelectionDropdown(assignees,labels[2],onCategorySelected = { category -> assigneeChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TasksDatePicker { date ->selectedDate.value = date}
            Spacer(modifier = Modifier.width(16.dp))
            SelectionDropdown(
                options = repetitionOptions,
                label = "Repeat",
                onCategorySelected = { repetitionChoice = it }
            )
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        var isErrorVisible by remember { mutableStateOf(false) }
        Button(
            onClick = {
                val allFieldsSelected =
                    choreChoice.isNotEmpty() &&
                            categoryChoice.isNotEmpty() &&
                            assigneeChoice.isNotEmpty() &&
                            selectedDate.value != null &&
                            repetitionChoice != "None"
                if (allFieldsSelected) {
                    val dueDate = selectedDate.value ?: return@Button // Ensure due date is not null

                    val repetitions = when (repetitionChoice) {
                        "Every Day" -> {
                            val endDate = dueDate.plusMonths(4) // Get the end date 4 months from the due date
                            val daysBetween = ChronoUnit.DAYS.between(dueDate, endDate)
                            (daysBetween / 7) * 7 // 7 times per week for 4 months
                        }

                        "Week" -> 16 // Once per week for 4 months (4 weeks * 4 months)
                        "2 Weeks" -> 8 // Once per two weeks for 4 months (2 weeks * 4 months)
                        "3 Weeks" -> 4 // Once per three weeks for 4 months (1 week * 4 months)
                        "4 Weeks" -> 4 // Once per four weeks for 4 months (1 week * 4 months)
                        else -> 1 // Default to one-time chore
                    }

                    repeat(repetitions.toInt()) {
                        val choreDueDate = when (repetitionChoice) {
                            "Every Day" -> dueDate.plusDays(it.toLong()) // Add days for "Every Day"
                            else -> dueDate.plusWeeks(it.toLong()) // Add weeks for other repetitions
                        }

                        val chore = Chore(
                            userId = userId ?: "",
                            choreId = "$choreId-$it", // Ensure unique ID for each chore
                            choreName = choreChoice,
                            category = categoryChoice,
                            assignee = assigneeChoice,
                            dueDate = choreDueDate, // Assign due date for the chore
                            userRating = emptyList(),
                            votedUser = emptyList()
                        )
                        createChore(chore)
                        choreRepository.createChore(chore)
                            .addOnSuccessListener {
                                Log.d("ChoreCreation", "Chore successfully added to Firestore")
                                choreCounter++
                                if (choreCounter == repetitions.toInt()) {
                                    onDialogDismiss()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("ChoreCreation", "Error adding chore to Firestore", e)
                            }
                    }


                }else {
                    isErrorVisible = true
                }
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
        if (isErrorVisible) {
            AlertDialog(
                onDismissRequest = { isErrorVisible = false },
                title = { Text("Missing Fields!") },
                text = { Text("Please select all fields before creating a chore.") },
                confirmButton = {
                    TextButton(onClick = { isErrorVisible = false }) {
                        Text("Okay")
                    }
                }
            )
        }
    }
}