package org.housemate.presentation.userinterface.authentication

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


data class Task(val name: String)


@Composable
fun MainLayout(navController: NavController) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 15.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Text(
                "Welcome to HouseMate",
                modifier = Modifier
                    .padding(top = 20.dp, start = 35.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 60.sp
            )

            Text(
                "Create a New Household",
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )
            Text(
                "Start a new household and invite your housemates!",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 14.sp
            )
            Button(
                onClick = {
                    // Add new page
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Create Household")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 65.dp)
        ) {
            Text(
                "Join an Existing Household",
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )
            Text(
                "Enter the code sent by your housemate!",
                modifier = Modifier
                    .padding(top = 10.dp, start = 0.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 14.sp
            )
            Button(
                onClick = {
                    // Add new page
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Enter Here")
            }
        }
    }
}


@Composable
fun SetupUsernameScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}