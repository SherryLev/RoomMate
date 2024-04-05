package org.housemate.presentation.userinterface.chores


import android.content.ContentValues.TAG
import android.util.Log
import android.widget.GridLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.Chore
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
//import com.google.android.libraries.places.api.model.LocalDate
import org.housemate.R
import org.housemate.data.firestore.ChoreRepositoryImpl
import org.housemate.presentation.sharedcomponents.TextEntryModule
import org.housemate.presentation.userinterface.home.Section
import org.housemate.theme.md_theme_light_primary
import org.housemate.theme.starColor
import java.time.DayOfWeek
import kotlin.math.sqrt
import org.housemate.data.firestore.UserRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.presentation.userinterface.expenses.CustomDropdown
import org.housemate.presentation.viewmodel.ChoresViewModel
import java.time.LocalDate
import java.util.Calendar

//var choresList = mutableListOf<Chore>()

//CITE: https://medium.com/@imitiyaz125/star-rating-bar-in-jetpack-compose-5ae54a2b5b23
@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
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
fun TaskItem(chore: Chore, choresViewModel: ChoresViewModel = hiltViewModel(),chorePrefix: String, userId: String) {
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
                       choresViewModel.deleteMultipleChores(chorePrefix, userId)
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
fun TaskWeekItem(chore: Chore) {
    var rating by remember { mutableStateOf(1f) }
    Log.d("Rating", "Rating is: " + rating)
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .height(80.dp),
        elevation = 5.dp
    ) {
        Column(

            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.person),
                contentDescription = "local",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = chore.assignee,
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.Center
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 30.dp, top = 16.dp)
        ) {
            Text(
                text = chore.choreName,
                style = TextStyle(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 18.dp)
            )
            StarRatingBar(maxStars = 5, rating = rating, onRatingChanged = { rating = it })
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
fun TaskDisplayHouse(chores: List<Chore>, choresViewModel: ChoresViewModel = hiltViewModel()) {
    val uniqueChoreTypes = mutableSetOf<String>()

    LazyColumn(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        items(chores) { chore ->
            val choreType = extractChoreType(chore.choreId)
            print(choreType)
            if (uniqueChoreTypes.add(choreType)) { // Check if the chore type is already added
                val chorePrefix = chore.choreId.substringBefore("-") + "-"
                TaskItem(chore, choresViewModel,chorePrefix, chore.assigneeId)
                Spacer(modifier = Modifier.height(8.dp))
            }
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
               choresViewModel: ChoresViewModel = hiltViewModel()) {
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
    val stringDays = listOf("Week", "Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday")
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
                        disabledBackgroundColor = md_theme_light_primary,
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
                        disabledBackgroundColor = md_theme_light_primary,
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
               if(!isHouse) {
                   CustomDropdown(
                       stringDays,
                       selectedDay,
                       onItemSelected = { selectedDay = it },
                       modifier = Modifier ,
                       dropdownWidth = 128.dp
                   )
               }
           }
            Column(
                ) {
                if(isHouse){
                    TaskDisplayHouse(chores)
                } else {
                    val currentWeekTasks = getCurrentWeekTasks(chores)
                    if(selectedDay == "Week") {
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
                                    val camelCaseDay = day.toString().lowercase().replaceFirstChar { it.uppercase() }

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
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    } else{
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
        if(isHouse) {
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
            Dialog( onDismissRequest = {
                showDialog = false
                choresViewModel.setDialogDismissed(true)
            }) {
                // Content of the modal dialog
                Box(
                    modifier = Modifier
                        .background(Color.White)
                    //.padding(16.dp)
                ) {
                    ChoreCreator(
                        onDialogDismiss = {
                            showDialog = false
                        } )
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 10.dp, start = 16.dp)
                    ) {
                        Text("Cancel")
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}

// Function to extract chore type from choreId
private fun extractChoreType(choreId: String): String {
    // Split the choreId by "-" and get the first part
    return choreId.split("-").firstOrNull() ?: ""
}


