package org.housemate.presentation.userinterface.authentication


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.presentation.userinterface.chores.ChoreCreator
import org.housemate.presentation.userinterface.chores.choresList


data class Task(val name: String)




@Composable
fun MainLayout(navController: NavController) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var showDialog by remember { mutableStateOf(false) }
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
                    showDialog = true
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Create Household")
            }
        }
        if (showDialog) {
            Dialog( onDismissRequest = { showDialog = false }) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(70.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Enter the name of the HouseHold here:",
                        modifier = Modifier.align(Alignment.TopCenter),
                        fontSize = 14.sp
                    )
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
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Enter Here")
            }

            Button(
                onClick = {
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Finish Setup")
            }
        }
    }
}




@Composable
fun SetupScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(navController = navController)
    }
}
