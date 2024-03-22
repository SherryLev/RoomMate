package org.housemate.presentation.userinterface.stats


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.utils.AppScreenRoutes


data class Task(val name: String)

@Composable
fun MainLayout(navController: NavController) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        ) {

            Text(
                "Statistics",
                modifier = Modifier
                    .padding(top = 20.dp, start = 6.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 60.sp
            )

            Text(
                "View your spending history below",
                modifier = Modifier
                    .padding(top = 30.dp, start = 6.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )
            Text(
                "See your spending history over the last:",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 14.sp
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("1 Week")
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("2 Weeks")
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("1 Month")
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("2 Months")
                }
            }

            Button(
                onClick = { println("pressed") },
                shape = CutCornerShape(percent = 0),
                enabled = true,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Show Graph Here")
            }

            Text(
                "Your Average Chore Rating is:",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 18.sp
            )

            Button(
                onClick = { println("pressed") },
                shape = CutCornerShape(percent = 0),
                enabled = true,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Insert Rating Here in Gold")
            }
        } // testing
    }
}



@Composable
fun StatsScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}
