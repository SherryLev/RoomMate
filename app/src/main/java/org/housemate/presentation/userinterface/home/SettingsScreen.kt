package org.housemate.presentation.userinterface.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.presentation.sharedcomponents.TextEntryModule
import org.housemate.presentation.viewmodel.ExpenseViewModel
import org.housemate.presentation.viewmodel.SettingsViewModel
import org.housemate.theme.md_theme_light_error
import org.housemate.theme.md_theme_light_primary
import org.housemate.utils.AuthScreen
import org.housemate.utils.Graph

@Composable
fun SettingsScreen(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToAuthScreen: () -> Unit
) {

    val logoutState by settingsViewModel.logoutState.collectAsState()

    // Observe logout state
    if (logoutState == true) {
        onNavigateToAuthScreen()
        settingsViewModel.resetLogoutState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }, content = {paddingValues ->
            Box(
                modifier = (Modifier.padding(top = paddingValues.calculateTopPadding()))
            ) {
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Section("Username") {
                        // Display current username
                        Text("Your current username is: [currentUsername]", modifier = Modifier.padding(vertical = 8.dp))

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        TextEntryModule(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            description = "Edit username",
                            hint = "Enter new username",
                            textValue = "",
                            textColor = Color.Gray,
                            cursorColor = md_theme_light_primary,
                            onValueChanged = { /* TODO */ },
                            trailingIcon = null,
                            onTrailingIconClick = null,
                            leadingIcon = Icons.Default.AccountCircle
                        )

                        Button(
                            onClick = { /* Handle username change */ },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Change Username", fontSize = 16.sp)
                        }
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                item {
                    // View Group Code
                    Section("Your Group Code") {
                        Text("ABC123", modifier = Modifier.padding(vertical = 8.dp))
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                item {
                    Section("Join a Different Group") {
                        Text(
                            "Joining a different group will erase all of the data from this group.",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        TextEntryModule(
                            modifier = Modifier
                                .fillMaxWidth(),
                            description = "Enter valid group code",
                            hint = "Group code",
                            textValue = "",
                            textColor = Color.Gray,
                            cursorColor = md_theme_light_primary,
                            onValueChanged = { /* TODO */ },
                            trailingIcon = null,
                            onTrailingIconClick = null,
                            leadingIcon = Icons.Default.Groups
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Button(
                            onClick = { /* Handle join group */ },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)

                        ) {
                            Text("Switch groups", fontSize = 16.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }

                item {
                    Section("Log out of your account") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Button(
                            onClick = { settingsViewModel.logout() },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Log Out", fontSize = 16.sp)
                        }
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                item {
                    // Delete Account
                    Section("Delete Account") {
                        Text(
                            "Deleting your account will permanently remove all your data. Are you sure?",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { /* Handle account deletion */ },
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = md_theme_light_error,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Delete Account", fontSize = 16.sp)
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        content()
    }
}