package org.housemate.presentation.userinterface.chores


import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.sqrt
var choresList = mutableListOf<Chore>()


@Composable
fun TaskItem(chore: Chore) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = chore.dueDate?.format(formatter)
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(70.dp)
            .background(Color.LightGray),
       contentAlignment = Alignment.Center
    ) {
        Text(
            text = chore.choreName,
            style = TextStyle(fontSize = 20.sp),
            textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.height(8.dp))
    }
    /*Card(
        backgroundColor = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = chore.choreName,
                style = TextStyle(fontSize = 20.sp)
            )
            //Text(text = "Due: $formattedDateTime")
            //Text(text = "Chore: ${chore.choreName}")
            //Text(text = "Category: ${chore.category}")
            //Text(text = "Assignee: ${chore.assignee}")
            Spacer(modifier = Modifier.height(8.dp))

        }
    }*/
}

@Composable
fun TaskDisplayArea(chores: List<Chore>, deleteTask: (Chore) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(chores){chore ->
            TaskItem(chore)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



@Composable
fun MainLayout(navController: NavHostController = rememberNavController()) {
    val chores = remember { choresList }
    var showDialog by remember { mutableStateOf(false) }
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
                    onClick = { println("pressed") },
                    shape = CutCornerShape(percent = 0),
                    enabled = true,
                    modifier = Modifier
                        //.padding(horizontal = 16.dp)
                        .fillMaxWidth(0.4f)
                ) {
                    Text("Personal")
                }
                Button(
                    onClick = { println("pressed") },
                    shape = CutCornerShape(percent = 0),
                    enabled = false,
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
                Text(
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
                )
                TaskDisplayArea(chores, deleteTask = {chore -> chores.remove(chore) })
            }
        }

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

