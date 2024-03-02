package org.housemate.presentation.userinterface.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


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
fun MainLayout(navController: NavController) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 16.dp, top = 5.dp, end = 16.dp, bottom = 18.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { println("pressed") },
                    shape = CutCornerShape(percent = 0),
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Personal")
                }
                Button(
                    onClick = { println("pressed") },
                    shape = CutCornerShape(percent = 0),
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("House")
                }
            }
            Text(
                "You Are Owed:",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally), // Center the text horizontally
                fontSize = 20.sp
            )
            Text(
                "This is who owes you how much:",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally), // Center the text horizontally
                fontSize = 20.sp
            )
            // TaskList added here
            TaskList(tasks)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    val newTask = Task("Person")
                    tasks = tasks + listOf(newTask)
                    println("+ pressed")
                }
            ) {
                Text("+ Add Expense")
            }
        }
    }
}


@Composable
fun ExpensesScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
        //Text(text = "Expenses screen") // comment this out or make empty string
    }
}