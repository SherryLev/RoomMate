package org.housemate.presentation.userinterface.chores


import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlin.math.sqrt
data class Task(val name: String)

@Composable
fun TaskSquare(task: Task) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = task.name, color = Color.White)
    }
}
@Composable
fun TaskList(tasks: List<Task>) {
    LazyColumn {
        items(tasks) { task ->
            TaskSquare(task = task)
        }
    }
}
@Composable
fun MainLayout(navController: NavHostController = rememberNavController()) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
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
                // TaskList added here
                TaskList(tasks)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    val newTask = Task("New Task")
                    tasks = tasks + listOf(newTask)
                    println("+ pressed")
                }
            ) {
                Text("+ Create Chore")
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

