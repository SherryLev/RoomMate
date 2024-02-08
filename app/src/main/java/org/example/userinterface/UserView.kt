package org.example.userinterface

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.example.controller.UserController

enum class ViewEvent {
    FirstNameEvent,
    LastNameEvent,
    UppercaseEvent,
    LowercaseEvent,
    ResetEvent
}

@Composable
fun UserView(userViewModel: UserViewModel, userController: UserController) {
    val viewModel by remember { mutableStateOf(userViewModel) }
    val controller by remember { mutableStateOf(userController) }

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        TextField(
            viewModel.firstname.value,
            label = {Text("First Name: ")},
            onValueChange = { controller.invoke(ViewEvent.FirstNameEvent, it) })
        TextField(
            viewModel.lastname.value,
            label = {Text("Last Name: ")},
            onValueChange = { controller.invoke(ViewEvent.LastNameEvent, it) })

        Row (horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                controller.invoke(ViewEvent.UppercaseEvent, null)
            }) {
                Text("UPPER")
            }
            Button(onClick = {
                controller.invoke(ViewEvent.LowercaseEvent, null)
            }) {
                Text("lower")
            }
            Button(onClick = {
                controller.invoke(ViewEvent.ResetEvent, null)
            }) {
                Text("Reset")
            }
        }
    }
}