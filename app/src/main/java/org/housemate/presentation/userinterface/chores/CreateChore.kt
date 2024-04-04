package org.housemate.presentation.userinterface.chores

import android.app.DatePickerDialog
import android.text.Selection
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import org.housemate.R
import org.housemate.domain.model.Chore
import org.housemate.domain.model.User
import java.util.*
import org.housemate.domain.repositories.ChoreRepository
import org.housemate.domain.repositories.UserRepository
import org.housemate.presentation.userinterface.expenses.CustomDropdown
import org.housemate.presentation.viewmodel.ChoresViewModel
import org.housemate.presentation.viewmodel.ExpenseViewModel
import java.time.temporal.ChronoUnit

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
fun TasksDatePicker(onDateSelected: (Timestamp) -> Unit): Timestamp? {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Getting today's date fields
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]

    var selectedDateText by remember { mutableStateOf("") }

    val datePicker =
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                val selectedTimestamp = Timestamp(Date(selectedYear - 1900, selectedMonth, selectedDay))
                onDateSelected(selectedTimestamp)
            },
            year,
            month,
            day,
        )
    // Can't pick dates in the past
    datePicker.datePicker.minDate = calendar.timeInMillis

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        ReadonlyOutlinedTextField(
            value = selectedDateText,
            onValueChange = { selectedDateText = it },
            onClick = {
                datePicker.show()
            }
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
            readOnly = true,
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
fun alignButton(options: List<String>,label: String,onCategorySelected: (String) -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            Icon(painterResource(R.drawable.add), "Add")
        }
        Spacer(modifier = Modifier.width(12.dp))
        SelectionDropdown(options,label, onCategorySelected)
    }
}

@Composable
fun alignButtonforUser(
    housemates: List<User>,
    label: String,
    onHousemateSelected: (User) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            Icon(painterResource(R.drawable.add), "Add")
        }
        Spacer(modifier = Modifier.width(12.dp))
        SelectionDropdown(
            options = housemates.map { it.username }, // Map User objects to their usernames
            label = label,
            onCategorySelected = { username ->
                val selectedHousemate = housemates.find { it.username == username }
                selectedHousemate?.let { onHousemateSelected(it) }
            }
        )
    }
}

@Composable
fun ChoreCreator(onDialogDismiss: () -> Unit,
                 choresViewModel: ChoresViewModel = hiltViewModel()
){
    val housemates by choresViewModel.housemates.collectAsState()

    val categories = listOf( "Kitchen", "Living Room", "Dining Room", "Staircase", "Backyard")
    val choreList = listOf( "Clean dishes", "Sweep Floors", "Clean Toilet", "Vacuum Floor")
    val labels =  listOf("Chore","Category", "Assignee" )
    var categoryChoice by remember { mutableStateOf("") }
    var choreChoice by remember { mutableStateOf("") }
    var assigneeChoice by remember { mutableStateOf("") }
    var assigneeId by remember { mutableStateOf("") }

    val selectedDate = remember { mutableStateOf<Timestamp?>(null) }
    val repetitionOptions = listOf("None", "Every day", "Every 2 days", "Every 3 days", "Every week", "Every 2 wks", "Every 3 wks", "Every 4 wks")
    var repetitionChoice by remember { mutableStateOf("None") }
    var choreCounter by remember { mutableStateOf(0) }
    val choreId = UUID.randomUUID().toString() // Generate unique chore ID

    val dialogDismissed by choresViewModel.dialogDismissed.collectAsState()
    val userId by choresViewModel.userId.collectAsState()

    LaunchedEffect(dialogDismissed) {
        if (dialogDismissed) {
            choresViewModel.getAllChores()
            choresViewModel.setDialogDismissed(false) // Reset the state after refreshing
        }
    }

    LaunchedEffect("fetchUserId") {
        choresViewModel.fetchCurrentUserId()
        choresViewModel.fetchAllHousemates()
    }

    println(housemates)

    Column(
        modifier = Modifier.padding(16.dp)) {
        alignButton(choreList, labels[0],onCategorySelected = { category -> choreChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        alignButton(categories,labels[1], onCategorySelected = { category -> categoryChoice = category })
        Spacer(modifier = Modifier.height(16.dp))
        alignButtonforUser(housemates, labels[2]) { housemate ->
            assigneeChoice = housemate.username
            assigneeId = housemate.uid
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TasksDatePicker { date ->selectedDate.value = date }
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
                            selectedDate.value != null
                if (allFieldsSelected) {
                    val dueDate = selectedDate.value ?: return@Button // Ensure due date is not null

                    val calendar = Calendar.getInstance()
                    calendar.time = dueDate.toDate() // Convert to Date type

                    val repetitions = when (repetitionChoice) {
                        "Every day" -> {
                            calendar.add(Calendar.MONTH, 4) // Add 4 months to the due date
                            val endDate = calendar.time // Get the end date
                            val daysBetween = (endDate.time - dueDate.toDate().time) / (1000 * 60 * 60 * 24)
                            (daysBetween / 7) * 7
                        }
                        "Every 2 days", "Every 3 days" -> {
                            // Calculate the end date based on the chosen repetition duration
                            val interval = when (repetitionChoice) {
                                "Every 2 days" -> 2
                                "Every 3 days" -> 3
                                else -> 1 // Default to 1 day if not specified
                            }
                            calendar.add(Calendar.MONTH, 4) // Add 4 months to the due date
                            val endDate = calendar.time // Get the end date
                            val daysBetween = (endDate.time - dueDate.toDate().time) / (1000 * 60 * 60 * 24)
                            daysBetween / interval // Calculate the number of repetitions based on the interval
                        }
                        "Every week" -> 16
                        "Every 2 wks" -> 8
                        "Every 3 wks" -> 4
                        "Every 4 wks" -> 4
                        else -> 1
                    }
                    repeat(repetitions.toInt()) { index ->
                        val repetitionSeconds = when (repetitionChoice) {
                            "Every 2 days" ->  86400 * 2 * index
                            "Every 3 days" -> 86400 * 3 * index
                            "Every day" -> 86400 * index
                            "Every 2 wks" -> 604800 * 2 * index
                            "Every 3 wks" -> 604800 * 3 * index
                            "Every 4 wks" -> 604800 * 4 * index
                            else -> 604800 * index
                        }

                        val choreDueDate = Timestamp(dueDate.seconds + repetitionSeconds, dueDate.nanoseconds)
                        // Create the chore with the calculated due date
                        println(choreDueDate.toDate())

                        val chore = Chore(
                            userId = userId ?: "",
                            choreId = "$choreId-$index", // Ensure unique ID for each chore
                            choreName = choreChoice,
                            category = categoryChoice,
                            assignee = assigneeChoice,
                            assigneeId = assigneeId,
                            dueDate = choreDueDate,
                            userRating = emptyList(),
                            votedUser = emptyList(),
                            repeat = repetitionChoice
                        )
                        choresViewModel.addChore(chore)
                        choreCounter++
                    }
                    choresViewModel.setDialogDismissed(true)
                    onDialogDismiss()

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