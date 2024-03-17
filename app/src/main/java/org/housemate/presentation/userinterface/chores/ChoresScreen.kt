package org.housemate.presentation.userinterface.chores


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import org.housemate.R
import org.housemate.presentation.sharedcomponents.TextEntryModule
import org.housemate.presentation.userinterface.home.Section
import org.housemate.theme.md_theme_light_primary
import org.housemate.theme.starColor
import java.time.DayOfWeek
import kotlin.math.sqrt
var choresList = mutableListOf<Chore>()

@Composable /// CITE this https://stackoverflow.com/questions/73948960/jetpack-compose-how-to-create-a-rating-bar
private fun RatingBarComposable() {
    var rating by remember { mutableStateOf(0) }
    val outlinedStar = painterResource(id = R.drawable.outlined_star)
    val filledStar = painterResource(id = R.drawable.filled_star)
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(5) { index ->
            Icon (
                //imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                painter = if (index < rating) filledStar else outlinedStar,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        rating = index + 1
                    }
                    .padding(4.dp)
            )
        }
    }
}
@Composable
fun TaskItem(chore: Chore) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = chore.dueDate?.format(formatter)
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
           // contentColor = MaterialTheme.colors. // Set content color if needed
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(300.dp)
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 4.dp, end = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "local",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = chore.assignee,
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = chore.choreName,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 24.dp)
                )

               /* Text(
                    text = chore.dueDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "No due date",
                    style = TextStyle(fontSize = 10.sp)
                )*/
            }
        }
    }

        Spacer(modifier = Modifier.height(8.dp))
}
@Composable
fun TaskWeekItem(chore: Chore) {
    Card(
       // backgroundColor = Color(lightPurple),
        modifier = Modifier
            .width(300.dp)
            .height(50.dp)
    ) {
        Column(
            //horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
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
        ) {
            Text(
                text = chore.choreName,
                style = TextStyle(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 24.dp)
            )

            /* Text(
                 text = chore.dueDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "No due date",
                 style = TextStyle(fontSize = 10.sp)
             )*/
            RatingBarComposable()
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TaskDisplayHouse(chores: List<Chore>, deleteTask: (Chore) -> Unit) {
    LazyColumn(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        items(chores){chore ->
            TaskItem(chore)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun TaskDisplayWeek(chores: List<Chore>, deleteTask: (Chore) -> Unit, day: DayOfWeek) {
    LazyColumn(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        items(chores){chore ->
            if(chore.dueDate?.dayOfWeek == day){
                TaskWeekItem(chore)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun MainLayout(navController: NavHostController = rememberNavController()) {
    val chores = remember { choresList }
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
            Column(
                //modifier = Modifier.align(Alignment.Start)
            ) {
                if(isHouse){
                    TaskDisplayHouse(chores, deleteTask = {chore -> chores.remove(chore) })
                } else {
                    for (day in daysOfWeekList) {
                        Column(modifier = Modifier.weight(1f)) { // Set weight to 1
                            Text(text = day.toString())
                            Spacer(modifier = Modifier.height(4.dp))
                            TaskDisplayWeek(chores, deleteTask = { chore -> chores.remove(chore) }, day)
                            Spacer(modifier = Modifier.height(4.dp))
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        // val newTask = Task("New Task")
                        //  tasks = tasks + listOf(newTask)
                        showDialog = true
                    }
                ) {
                    Text("+ Create Chore")
                }
            }
        }
        if (showDialog) {
            Dialog( onDismissRequest = { showDialog = false }) {
                // Content of the modal dialog
                Box(
                    modifier = Modifier
                        .background(Color.White)
                    //.padding(16.dp)
                ) {
                    ChoreCreator(addChore = { chore -> choresList.add(chore) }, onDialogDismiss = { showDialog = false })
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
fun ChoresScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}


