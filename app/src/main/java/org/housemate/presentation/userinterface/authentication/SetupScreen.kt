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
import androidx.compose.runtime.rememberCoroutineScope
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
import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.model.Group
import org.housemate.domain.repositories.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType



data class Task(val name: String)




@Composable
fun MainLayout( onNavigateToHomeScreen: () -> Unit, navController: NavController, groupRepository: GroupRepository, userRepository: UserRepository) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var showDialog by remember { mutableStateOf(false) }
    var coroutineScope = rememberCoroutineScope()

    // States for new group creation
    var groupName by remember { mutableStateOf("")}
    var showGroupCodeDialog by remember { mutableStateOf(false) }
    var groupCode by remember { mutableStateOf("") }
    var errorCreatingGroup by remember { mutableStateOf<String?>(null) }

    var showSuccessMessage by remember { mutableStateOf(false)}
    var showErrorMessage by remember { mutableStateOf("")}

    var userId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userId = userRepository.getCurrentUserId()
    }


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

                    // Initiate the process to create a new group
                    coroutineScope.launch {
                        val uniqueGroupCode = UUID.randomUUID().toString().replace("-","").substring(0,4)
                        // group code will be of length 4
                        val newGroup = Group(uniqueGroupCode, "groupName", userId, listOf(userId))
                        try {
                            val success = groupRepository.createGroup(newGroup)
                            if (success) {
                                val userDocRef = FirebaseFirestore.getInstance().collection("users").document(
                                    userId.toString()
                                )
                                userDocRef.update("groupCode", uniqueGroupCode).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showSuccessMessage = true
                                    } else {
                                        showErrorMessage = "Failed to update user with group code. Please try again"
                                    }
                                }

                                showSuccessMessage = true
                                groupCode = uniqueGroupCode
                                showGroupCodeDialog = true

                            } else {
                                showErrorMessage = "Failed to create group. Please try again"
                            }
                        } catch (e: Exception) {
                            showErrorMessage = "An error occured: ${e.message}"
                        }
                    }
                    //showDialog = true
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
            // State to hold the group code entered by the user
            var enteredGroupCode by remember { mutableStateOf("")}

            TextField(
                value = enteredGroupCode,
                onValueChange = { enteredGroupCode = it },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(200.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            // Get group code from Firestore using the enteredGroupCode
                            val group = groupRepository.getGroupByCode(enteredGroupCode)

                            if (group != null) {
                                // Add current user to the group members
                                //val updateResult = groupRepository.addMemberToGroup(enteredGroupCode, userId)
                                val success = groupRepository.addMemberToGroup(enteredGroupCode, userId!!)
                                if (success) {
                                    val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId!!)
                                    userDocRef.update("groupCode", enteredGroupCode).addOnSuccessListener {
                                        showSuccessMessage = true
                                        // NAVIGATE TO NEXT SCREEN
                                    }.addOnFailureListener{
                                        showErrorMessage = "Failed to update user with group code. Please try again"
                                    }
                                } else {
                                    showErrorMessage =
                                        "Failed to join the group. Please check the code and try again."
                                }
                            } else {
                                showErrorMessage =
                                    "No group found with that code. Please check the code and try again"
                            }

                        } catch (e: Exception) {
                            showErrorMessage = "An error occurred: ${e.message}"
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 25.dp)
            ) {
                Text("Enter Here")
            }

            Button(
                onClick = {
                    onNavigateToHomeScreen()
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
fun SetupScreen( onNavigateToHomeScreen: () -> Unit,
                 navController: NavHostController = rememberNavController(),
                 groupRepository: GroupRepository,
                 userRepository: UserRepository) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainLayout(onNavigateToHomeScreen,
            navController = navController,
            groupRepository = groupRepository,
            userRepository = userRepository)
    }
}
