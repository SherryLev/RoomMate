package org.housemate.presentation.userinterface.chores

import android.app.DatePickerDialog
import android.text.Selection
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.housemate.presentation.viewmodel.ChoresViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import org.housemate.theme.lavender
import org.housemate.theme.light_gray
import org.housemate.theme.light_purple
import org.housemate.theme.light_purple_background
import org.housemate.theme.pretty_purple
import org.housemate.theme.purple_gray_background
import org.housemate.theme.purple_primary
import org.housemate.utils.HomeNavGraph

@Composable
fun ReadonlyTextField(
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
            modifier = modifier
                .width(120.dp),
            enabled = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                focusedBorderColor = purple_primary,
                unfocusedBorderColor = purple_primary
            ),
            textStyle = TextStyle(textAlign = TextAlign.Center, color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 16.sp),
            label = label,
            shape = RoundedCornerShape(25.dp) // Set curved corners
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
        ReadonlyTextField(
            value = selectedDateText,
            onValueChange = { selectedDateText = it },
            onClick = {
                datePicker.show()
            }
        ) {
            Text(text = "Start Date", color = purple_primary, textAlign = TextAlign.Center)
        }
    }
    return null
}

@Composable
fun SelectionDropdown(
    options: List<String>,
    label: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(label) }
    val boxWidth = remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(color = light_gray)
            .clickable { expanded = !expanded }
            .padding(8.dp)
            .onSizeChanged { boxWidth.value = it.width } // Measure the width of the Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
                color = purple_primary,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = purple_primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(0.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { boxWidth.value.toDp() }) // Set width equal to the Box width
                .heightIn(max = 240.dp) // Set max height for the dropdown menu
        ) {
            Column {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = option
                            onCategorySelected(option)
                            expanded = false
                        }
                    ) {
                        Text(
                            text = option,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomChoreDialog(
    title: String,
    content: @Composable () -> Unit,
    confirmButtonText: String,
    onConfirmButtonClicked: () -> Unit,
    dismissButtonText: String,
    onDismissButtonClicked: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissButtonClicked() },
        title = { Text(title) },
        text = content,
        confirmButton = {
            Button(
                onClick = {
                    onConfirmButtonClicked()
                    onDismissButtonClicked()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(onClick = { onDismissButtonClicked() }) {
                Text(dismissButtonText)
            }
        }
    )
}

@Composable
fun alignButton(options: List<String>, label: String, onCategorySelected: (String) -> Unit, choresViewModel: ChoresViewModel, snackbarHostState: SnackbarHostState) {

    var choreTypeText by remember { mutableStateOf("") }
    var choreCategoryText by remember { mutableStateOf("") }

    val userId by choresViewModel.userId.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box (modifier = Modifier.width(240.dp)) {
            SelectionDropdown(options, label, onCategorySelected)
        }
        Spacer(modifier = Modifier.width(8.dp))

        if (label == "Chore") {
            if (showDialog) {
                // Add Chore Type Dialog
                AddCustomChoreDialog(
                    title = "Add Chore Type",
                    content = {
                        Column {
                            TextField(
                                value = choreTypeText,
                                onValueChange = { choreTypeText = it },
                                label = { Text("Add a custom chore") }
                            )
                        }
                    },
                    confirmButtonText = "Add Chore Type",
                    onConfirmButtonClicked = {
                        scope.launch {
                            try {
                                userId?.let {
                                    choresViewModel.addChoreType(
                                        choreTypeText,
                                        it
                                    )
                                }
                                    // Show the Snackbar after deletion is successful
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Custom chore type added successfully!",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                } catch (e: Exception) {
                                    val errorMessage =
                                        e.message ?: "Failed to add custom chore type."
                                    val result = snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                    // Handle Snackbar action if needed
                                }
                            }
                        showDialog = false
                    },
                    dismissButtonText = "Cancel",
                    onDismissButtonClicked = { showDialog = false }
                )
            }
        }else if (label == "Category") {
            if (showDialog) {
                // Add Chore Category Dialog
                AddCustomChoreDialog(
                    title = "Add Chore Category",
                    content = {
                        Column {
                            TextField(
                                value = choreCategoryText,
                                onValueChange = { choreCategoryText = it },
                                label = { Text("Add a custom category") }
                            )
                        }
                    },
                    confirmButtonText = "Add Chore Category",
                    onConfirmButtonClicked = {
                        scope.launch {
                            try {
                                userId?.let {
                                    choresViewModel.addChoreCategory(
                                        choreCategoryText,
                                        it
                                    )
                                }
                                // Show the Snackbar after deletion is successful
                                val result = snackbarHostState.showSnackbar(
                                    message = "Custom chore category added successfully!",
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Short
                                )
                            } catch (e: Exception) {
                                val errorMessage =
                                    e.message ?: "Failed add custom chore category."
                                val result = snackbarHostState.showSnackbar(
                                    message = errorMessage,
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        showDialog = false
                    },
                    dismissButtonText = "Cancel",
                    onDismissButtonClicked = { showDialog = false }
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, RoundedCornerShape(8.dp)) // Apply rounded corners to the background
        ) {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.AddBox,
                    "Add",
                    tint = purple_primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


@Composable
fun alignButtonforUser(
    housemates: List<User>,
    label: String,
    onHousemateSelected: (User) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SelectionDropdown(
            options = housemates.map { it.username }, // Map User objects to their usernames
            label = label,
            onCategorySelected = { username ->
                val selectedHousemate= housemates.find { it.username == username }
                selectedHousemate?.let { onHousemateSelected(it) }
            }
        )
    }
}

@Composable
fun ChoreCreator(onDialogDismiss: () -> Unit,
                 choresViewModel: ChoresViewModel = hiltViewModel(),
                 snackbarHostState: SnackbarHostState
) {
    val housemates by choresViewModel.housemates.collectAsState()

    val categories by choresViewModel.choreCategories.collectAsState()
    val choreList by choresViewModel.choreTypes.collectAsState()

    val labels = listOf("Chore", "Category", "Assignee")
    var categoryChoice by remember { mutableStateOf("") }
    var choreChoice by remember { mutableStateOf("") }
    var assigneeChoice by remember { mutableStateOf("") }
    var assigneeId by remember { mutableStateOf("") }

    val selectedDate = remember { mutableStateOf<Timestamp?>(null) }
    val repetitionOptions = listOf(
        "None",
        "Every day",
        "Every 2 days",
        "Every 3 days",
        "Every week",
        "Every 2 wks",
        "Every 3 wks",
        "Every 4 wks"
    )
    var repetitionChoice by remember { mutableStateOf("None") }
    var choreCounter by remember { mutableStateOf(0) }
    val choreId = UUID.randomUUID().toString() // Generate unique chore ID

    val dialogDismissed by choresViewModel.dialogDismissed.collectAsState()
    val userId by choresViewModel.userId.collectAsState()

    LaunchedEffect(dialogDismissed) {
        if (dialogDismissed) {
            choresViewModel.getAllChores()
            choresViewModel.setDialogDismissed(false) // Reset the state after refreshing
            snackbarHostState.showSnackbar(
                message = "Chore added successfully!",
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect("fetchUserId") {
        choresViewModel.fetchAllHousemates()
    }


    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        alignButton(
            choreList,
            labels[0],
            onCategorySelected = { category -> choreChoice = category },
            choresViewModel,
            snackbarHostState
        )
        Spacer(modifier = Modifier.height(16.dp))
        alignButton(
            categories,
            labels[1],
            onCategorySelected = { category -> categoryChoice = category },
            choresViewModel,
            snackbarHostState
        )
        Spacer(modifier = Modifier.height(16.dp))
        alignButtonforUser(housemates, labels[2]) { housemate ->
            assigneeChoice = housemate.username
            assigneeId = housemate.uid
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TasksDatePicker { date -> selectedDate.value = date }
            Spacer(modifier = Modifier.width(16.dp))
            SelectionDropdown(
                options = repetitionOptions,
                label = "Repeat",
                onCategorySelected = { repetitionChoice = it }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        var isErrorVisible by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onDialogDismiss() },
                modifier = Modifier
                    .height(46.dp)
                    .width(100.dp),
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

            Button(
                onClick = {
                    val allFieldsSelected =
                        choreChoice.isNotEmpty() &&
                                categoryChoice.isNotEmpty() &&
                                assigneeChoice.isNotEmpty() &&
                                selectedDate.value != null
                    if (allFieldsSelected) {
                        val dueDate =
                            selectedDate.value
                                ?: return@Button // Ensure due date is not null

                        val calendar = Calendar.getInstance()
                        calendar.time = dueDate.toDate() // Convert to Date type

                        val repetitions = when (repetitionChoice) {
                            "Every day" -> {
                                calendar.add(
                                    Calendar.MONTH,
                                    4
                                ) // Add 4 months to the due date
                                val endDate = calendar.time // Get the end date
                                val daysBetween =
                                    (endDate.time - dueDate.toDate().time) / (1000 * 60 * 60 * 24)
                                (daysBetween / 7) * 7
                            }

                            "Every 2 days", "Every 3 days" -> {
                                // Calculate the end date based on the chosen repetition duration
                                val interval = when (repetitionChoice) {
                                    "Every 2 days" -> 2
                                    "Every 3 days" -> 3
                                    else -> 1 // Default to 1 day if not specified
                                }
                                calendar.add(
                                    Calendar.MONTH,
                                    4
                                ) // Add 4 months to the due date
                                val endDate = calendar.time // Get the end date
                                val daysBetween =
                                    (endDate.time - dueDate.toDate().time) / (1000 * 60 * 60 * 24)
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
                                "Every 2 days" -> 86400 * 2 * index
                                "Every 3 days" -> 86400 * 3 * index
                                "Every day" -> 86400 * index
                                "Every 2 wks" -> 604800 * 2 * index
                                "Every 3 wks" -> 604800 * 3 * index
                                "Every 4 wks" -> 604800 * 4 * index
                                else -> 604800 * index
                            }

                            val choreDueDate =
                                Timestamp(
                                    dueDate.seconds + repetitionSeconds,
                                    dueDate.nanoseconds
                                )
                            val chore = Chore(
                                userId = userId ?: "",
                                choreId = "$choreId-$index", // Ensure unique ID for each chore
                                choreName = choreChoice,
                                category = categoryChoice,
                                assignee = assigneeChoice,
                                assigneeId = assigneeId,
                                dueDate = choreDueDate,
                                userRating = emptyMap(), // Initialize as an empty map instead of empty list
                                repeat = repetitionChoice
                            )
                            choresViewModel.addChore(chore)
                            choreCounter++
                        }
                        choresViewModel.setDialogDismissed(true)
                        onDialogDismiss()
                    } else {
                        isErrorVisible = true
                    }
                },
                modifier = Modifier
                    .height(46.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(25.dp),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(
                    "Create",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
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