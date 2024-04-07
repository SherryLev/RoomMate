package org.housemate.presentation.userinterface.chores


import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.Chore
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
//import com.google.android.libraries.places.api.model.LocalDate
import org.housemate.R
import org.housemate.theme.purple_primary
import java.time.DayOfWeek
import org.housemate.presentation.userinterface.expenses.CustomDropdown
import org.housemate.presentation.viewmodel.ChoresViewModel
import org.housemate.theme.light_purple
import org.housemate.theme.light_red
import org.housemate.theme.red_error
import java.util.Calendar

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Delete Confirmation") },
            text = { Text("Are you sure you want to delete this chore?") },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = light_purple,
                        contentColor = purple_primary
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = light_red,
                        contentColor = red_error
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)) {
                    Text("Cancel")
                }
            }
        )
    }
}


//CITE: https://medium.com/@imitiyaz125/star-rating-bar-in-jetpack-compose-5ae54a2b5b23
@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (11.5f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier
            .selectableGroup()
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color.LightGray
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize).height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
fun TaskItem(chore: Chore, choresViewModel: ChoresViewModel = hiltViewModel(),chorePrefix: String, userId: String, snackbarHostState: SnackbarHostState) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .height(80.dp),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "local",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = chore.assignee,
                    style = TextStyle(fontSize = 15.sp),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 30.dp) // Adjust the padding here
            )  {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = chore.choreName,
                        style = TextStyle(fontSize = 15.sp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = chore.category,
                        style = TextStyle(fontSize = 13.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = "Repeats: ${chore.repeat}",
                        style = TextStyle(fontSize = 13.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Left
                    )

                }
            }
            Box(modifier = Modifier.padding(end = 5.dp)) {
                IconButton(
                    onClick = {
                       setShowDialog(true)
                        //deleteTask(chore)
                        //deleteChore(chore.choreId, userId: String)
                    },
                    modifier = Modifier
                        .width(22.dp)
                        .height(22.dp)
                ) {
                    Icon(painterResource(R.drawable.delete), "Delete", tint = Color.DarkGray)
                }
            }
            DeleteConfirmationDialog(
                showDialog = showDialog,
                onConfirm = {
                    // Call your delete function here
                    scope.launch {
                        // Call delete function and wait for it to finish
                        try {
                            choresViewModel.deleteMultipleChores(chorePrefix, userId)

                            // Show the Snackbar after deletion is successful
                            val result = snackbarHostState.showSnackbar(
                                message = "Chore deleted successfully!",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        } catch (e: Exception) {
                            val errorMessage = e.message ?: "Failed to delete chore. Please try again."
                            val result = snackbarHostState.showSnackbar(
                                message = errorMessage,
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                            // Handle Snackbar action if needed
                        }
                    }
                },
                onDismiss = { setShowDialog(false) }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
fun getCurrentWeekTasks(chores: List<Chore>): List<Chore> {
    val today = Timestamp.now().toDate()
    val calendar = Calendar.getInstance()
    calendar.time = today
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    val offsetDays = when (dayOfWeek) {
        Calendar.MONDAY -> 0
        else -> dayOfWeek - Calendar.MONDAY
    }

    calendar.add(Calendar.DAY_OF_YEAR, -offsetDays)
    val startOfWeek = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    val endOfWeek = calendar.time

    return chores.filter { chore ->
        val choreDueDate = chore.dueDate?.toDate()
        choreDueDate in startOfWeek..endOfWeek
    }
}


@Composable
fun TaskWeekItem(chore: Chore, choresViewModel: ChoresViewModel = hiltViewModel()) {
    val userId by choresViewModel.userId.collectAsState()
    val (rating, setRating) = remember { mutableStateOf(chore.userRating[userId] ?: 0f) }

    println("UserId: "+ userId)
    println("choreID is:" + chore.choreId)
    val updateRating: (Float) -> Unit = { newRating ->
        setRating(newRating)
        // Update the rating in the ViewModel
        println(newRating)
        userId?.let { choresViewModel.updateChoreRating(chore, newRating, it) }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .height(80.dp),
        elevation = 5.dp
    ) {
        Row (modifier = Modifier
                .padding(start = 10.dp)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "local",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text(
                    text = chore.assignee,
                    style = TextStyle(fontSize = 14.sp),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                if (userId != chore.assigneeId) {
                    Text(
                        text = chore.choreName,
                        style = TextStyle(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Render the StarRatingBar only if the current user is not the assignee
                    StarRatingBar(maxStars = 5, rating = rating, onRatingChanged = updateRating)
                } else {
                    Text(
                        text = chore.choreName,
                        style = TextStyle(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
fun stringToDayOfWeek(dayString: String): DayOfWeek? {
    return when (dayString.lowercase()) { // Convert to lowercase for case-insensitive matching
        "monday" -> DayOfWeek.MONDAY
        "tuesday" -> DayOfWeek.TUESDAY
        "wednesday" -> DayOfWeek.WEDNESDAY
        "thursday" -> DayOfWeek.THURSDAY
        "friday" -> DayOfWeek.FRIDAY
        "saturday" -> DayOfWeek.SATURDAY
        "sunday" -> DayOfWeek.SUNDAY
        else -> null // Return null for invalid day or "Week"
    }
}

@Composable
fun TaskDisplayHouse(chores: List<Chore>, choresViewModel: ChoresViewModel = hiltViewModel(), snackbarHostState: SnackbarHostState) {
    val uniqueChoreTypes = mutableSetOf<String>()

    LazyColumn(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        items(chores) { chore ->
            val choreType = extractChoreType(chore.choreId)
            print(choreType)
            if (uniqueChoreTypes.add(choreType)) { // Check if the chore type is already added
                val chorePrefix = chore.choreId.substringBefore("-") + "-"
                TaskItem(chore, choresViewModel,chorePrefix, chore.assigneeId, snackbarHostState = snackbarHostState)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item{
            Spacer(modifier = Modifier.height(38.dp))
        }
    }
}

@Composable
fun TaskDisplayWeek(chores: List<Chore>, day: DayOfWeek) {
    Column(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        val choresForDay = chores.filter { chore ->
            val choreDueDate = chore.dueDate?.toDate()
            if (choreDueDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = choreDueDate
                val calendarDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
                calendarDayOfWeek == day.value
            } else {
                false
            }
        }
        if (choresForDay.isNotEmpty()) {
            // Display chores for the specified day
            choresForDay.forEach { chore ->
                TaskWeekItem(chore)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            // Display message when there are no chores for the specified day
            Text(
                text = "You have no chores on this day!",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun MainLayout(navController: NavHostController = rememberNavController(),
               choresViewModel: ChoresViewModel = hiltViewModel(), snackbarHostState: SnackbarHostState) {
    val dialogDismissed by choresViewModel.dialogDismissed.collectAsState()
    val chores by choresViewModel.chores.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isPersonal by remember { mutableStateOf(false) }
    var isHouse by remember { mutableStateOf(true) }
    val daysOfWeekList = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )
    val stringDays =
        listOf("Week", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
//    var chores by remember { mutableStateOf(choresList) }
    var selectedDay by remember { mutableStateOf("Week") }

    LaunchedEffect(dialogDismissed) {
        if (dialogDismissed) {
            choresViewModel.getAllChores()
            choresViewModel.setDialogDismissed(false) // Reset the state after refreshing
        }
    }



    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 15.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Button(
                    onClick = {
                        isPersonal = true
                        isHouse = false
                    },
                    shape = CutCornerShape(percent = 0),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.Gray,
                        disabledBackgroundColor = purple_primary,
                        disabledContentColor = Color.White
                    ),
                    enabled = !isPersonal,
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxWidth(0.4f)
                ) {
                    Text("This week")
                }
                Button(
                    onClick = {
                        isHouse = true
                        isPersonal = false
                    },
                    shape = CutCornerShape(percent = 0),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.Gray,
                        disabledBackgroundColor = purple_primary,
                        disabledContentColor = Color.White
                    ),
                    enabled = !isHouse,
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text("All Chores")
                }
            }
            Row(
                modifier = Modifier
                    .width(300.dp), // Set desired fixed width
                horizontalArrangement = Arrangement.End
            ) {
                if (!isHouse) {
                    CustomDropdown(
                        stringDays,
                        selectedDay,
                        onItemSelected = { selectedDay = it },
                        modifier = Modifier,
                        dropdownWidth = 128.dp
                    )
                }
            }
            Column(
            ) {
                if (isHouse) {
                    TaskDisplayHouse(chores, snackbarHostState = snackbarHostState)
                } else {
                    val currentWeekTasks = getCurrentWeekTasks(chores)
                    if (selectedDay == "Week") {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .width(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                            ) {
                                // Iterate over each day of the week
                                daysOfWeekList.forEach { day ->
                                    // Add a spacer between days
                                    val camelCaseDay = day.toString().lowercase()
                                        .replaceFirstChar { it.uppercase() }

                                    item {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = camelCaseDay,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    item {
                                        TaskDisplayWeek(
                                            currentWeekTasks,
//                                            deleteTask = { chore -> chores.remove(chore) },
                                            day
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    } else {
                        val dayOfWeek: DayOfWeek? = stringToDayOfWeek(selectedDay)
                        Box(
                            modifier = Modifier.width(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column() {
                                Spacer(modifier = Modifier.height(16.dp))

                                //Log.d("day conversion ", "Day: "+ dayOfWeek)
                                if (dayOfWeek != null) {
                                    TaskDisplayWeek(currentWeekTasks, dayOfWeek)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                            }

                        }
                    }
                }
            }


        }
        if (isHouse) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(2.dp),

                    onClick = {
                        showDialog = true
                    },
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text(
                        "+ Create Chore",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                    choresViewModel.setDialogDismissed(true)
                }
            ) {
                Card(
                    modifier = Modifier
                        .height(370.dp)
                        .width(320.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ChoreCreator(
                            onDialogDismiss = {
                                showDialog = false
                            },
                            snackbarHostState = snackbarHostState
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun ChoresScreen(
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = (Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MainLayout(navController = navController, snackbarHostState = snackbarHostState)
            }
        }
    }
}

// Function to extract chore type from choreId
private fun extractChoreType(choreId: String): String {
    // Split the choreId by "-" and get the first part
    return choreId.split("-").firstOrNull() ?: ""
}


