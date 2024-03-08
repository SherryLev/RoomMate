package org.housemate.presentation.userinterface.chores


import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import org.housemate.R
import org.housemate.theme.starColor
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
            .padding(16.dp),
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
                    .size(36.dp)
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
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        modifier = Modifier
            .width(300.dp)
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 5.dp)
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
                    modifier = Modifier.padding(end = 8.dp, top = 15.dp)
                )
                RatingBarComposable()
            }
            /*Text(
                text = "...",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 10.dp, start = 20.dp, bottom = 10.dp)
            )*/

        }
    }

        Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TaskDisplayArea(chores: List<Chore>, deleteTask: (Chore) -> Unit) {
    LazyColumn(modifier = Modifier.padding(start = 2.dp, top = 10.dp)) {
        items(chores){chore ->
            TaskItem(chore)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DayOfWeekText(day: String) {
    Text(
        text = day,
        modifier = Modifier.padding(top = 10.dp, bottom = 45.dp),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Blue
    )
}

@Composable
fun MainLayout(navController: NavHostController = rememberNavController()) {
    val chores = remember { choresList }
    var showDialog by remember { mutableStateOf(false) }
    var isPersonal by remember { mutableStateOf(false) }
    var isHouse by remember { mutableStateOf(true) }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

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
                    enabled = !isPersonal,
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxWidth(0.4f)
                ) {
                    Text("Personal")
                }
                Button(
                    onClick = {
                        isHouse = true
                        isPersonal = false
                              },
                    shape = CutCornerShape(percent = 0),
                    enabled = !isHouse,
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text("House")
                }
            }
            Column(
                modifier = Modifier.align(Alignment.Start)
            ) {
                if(isHouse){
                    Text(
                        "House Chores",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TaskDisplayArea(chores, deleteTask = {chore -> chores.remove(chore) })
                }else{
                    Text(
                        "This week:",
                        modifier = Modifier.padding(top = 10.dp, bottom = 15.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    for (day in daysOfWeek) {
                        DayOfWeekText(day = day)
                    }


                }
                /*Text(
                    "Overdue Chores",
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 20.sp
                )
                Box(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .size(width = 300.dp, height = 150.dp)
                        .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
                ) {
                    // Content of the Box
                }
                Text(
                    "Today:",
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 20.sp
                )*/

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

