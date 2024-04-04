package org.housemate.presentation.userinterface.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.repositories.UserRepository
import androidx.compose.foundation.layout.Column
//import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.sp

@Composable
fun GroupSuccessScreen(onNavigateToHomeScreen: () -> Unit,
                       navController: NavHostController = rememberNavController(),
                       groupRepository: GroupRepository,
                       userRepository: UserRepository
) {

    var userId by remember { mutableStateOf<String?>(null) }
    var groupId by remember { mutableStateOf<String?>(null)}
    var isCreator by remember { mutableStateOf<Boolean?>(null)}
    var houseName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userId = userRepository.getCurrentUserId()
        groupId = userRepository.getGroupCodeForUser(userId!!)
        isCreator = groupRepository.isCreator(userId!!, groupId!!)
        houseName = groupRepository.getGroupName(groupId!!)

    }

        // Replace with your actual UI implementation
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textStyle = MaterialTheme.typography.body1
        val messageStyle = textStyle.copy(fontSize = 18.sp)
        val houseStyle = textStyle.copy(fontSize = 30.sp)
        val groupCodeStyle = textStyle.copy(fontSize = 60.sp)

        if (isCreator != null) {
            if (isCreator!!) {
                Text(text = "You have successfully created Household:", style = messageStyle)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text = houseName ?: "", style = houseStyle)
                Spacer(modifier=Modifier.height(25.dp))
                Text(text = "Your group code is:", style = messageStyle)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text = groupId ?: "", style = groupCodeStyle)
            } else {
                Text(text = "You have successfully joined Household:", style = messageStyle)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text = houseName ?: "", style = houseStyle)
                Spacer(modifier=Modifier.height(25.dp))
                Text(text = "Your group code is", style = messageStyle)
                Spacer(modifier=Modifier.height(8.dp))
                Text(text = groupId ?: "", style = groupCodeStyle)
            }
        } else {
            Text(text = "Error with group creation please try again")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToHomeScreen,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go to Home")
        }
    }
}