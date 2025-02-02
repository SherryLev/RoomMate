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
import androidx.compose.material.AlertDialog
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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.housemate.presentation.sharedcomponents.TextEntryModule
import org.housemate.presentation.viewmodel.DeleteAccountResult
import org.housemate.presentation.viewmodel.SettingsViewModel
import org.housemate.theme.light_purple
import org.housemate.theme.red_error
import org.housemate.theme.purple_primary
import kotlinx.coroutines.launch

import org.housemate.domain.repositories.GroupRepository
import org.housemate.domain.repositories.UserRepository

@Composable
fun SettingsScreen(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToAuthScreen: () -> Unit,
    userRepository: UserRepository,
    groupRepository: GroupRepository
) {
    val coroutineScope = rememberCoroutineScope()

    val enteredGroupCode = remember { mutableStateOf("")}

    val deleteAccountState by settingsViewModel.deleteAccountState.collectAsState()

    val logoutState by settingsViewModel.logoutState.collectAsState()

    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // State to hold group code
    var groupCode by remember { mutableStateOf<String?>(null)}

    val username by settingsViewModel.username.collectAsState()
    val groupName by settingsViewModel.groupName.collectAsState()
    val groupMembers by settingsViewModel.members.collectAsState()

    var groupCodeValidationErrorMessage by remember { mutableStateOf<String?>(null) }
    var groupSwitchSuccessMessage by remember { mutableStateOf<String?>(null) }
    var alreadyInGroupMessage by remember { mutableStateOf<String?>(null) }
    val darkGreen = Color(0xFF006400)
    val lightPurple = Color(0xFF8307f0)

    LaunchedEffect(Unit) {
        val userId = userRepository.getCurrentUserId()
        groupCode = userRepository.getGroupCodeForUser(userId!!)
        settingsViewModel.getUsername(userId = userRepository.getCurrentUserId()!!)
        settingsViewModel.fetchUserGroupName(userId)
        settingsViewModel.fetchGroupMembers(userId)
    }

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
                        if (username != null) {
                            Text("Your current username is: $username", modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            // Display a loading indicator or placeholder text while username is being fetched
                            Text("Loading username...", modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                item {
                    // View Group Code
                    Section("Your Household Info") {
                        val code = groupCode
                        if (code != null) {
                            Text("Code: $code", modifier = Modifier.padding(top = 8.dp))
                            Text("Household: $groupName", modifier = Modifier.padding(vertical = 4.dp))
                            val memberList = groupMembers.map { it?.username ?: "" }.joinToString(", ")
                            Text("Housemates: $memberList", modifier = Modifier.padding(bottom = 4.dp))

                        } else {
                            Text("Loading...", modifier = Modifier.padding(vertical = 8.dp))
                        }
                        //Text("ABC123", modifier = Modifier.padding(vertical = 8.dp))
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
                            textValue = enteredGroupCode.value,
                            textColor = Color.Gray,
                            cursorColor = purple_primary,
                            onValueChanged = { newCode -> enteredGroupCode.value = newCode },
                            trailingIcon = null,
                            onTrailingIconClick = null,
                            leadingIcon = Icons.Default.Groups
                        )

                        // HERE
                        groupSwitchSuccessMessage?.let { successMessage ->
                            Text(
                                text = successMessage,
                                color = darkGreen,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        groupCodeValidationErrorMessage?.let { errorMessage ->
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    //.align(Alignment.CenterVertically)
                            )
                        }
                        alreadyInGroupMessage?.let { message ->
                            Text(
                                text = message,
                                color = lightPurple,
                                modifier = Modifier
                                    //.align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val newGroupCode = enteredGroupCode.value
                                    val currentUserId = userRepository.getCurrentUserId()
                                    if (newGroupCode.isNotBlank()) {
                                        val currentUserId = userRepository.getCurrentUserId()
                                        currentUserId?.let { userId ->
                                            val newGroup = groupRepository.getGroupByCode(newGroupCode)
                                            val currentGroupCode = userRepository.getGroupCodeForUser(userId)

                                            if (newGroup != null) {
                                                val currentGroupCode = userRepository.getGroupCodeForUser(userId)
                                                if (currentGroupCode != null) {
                                                    groupRepository.removeMemberFromGroup(currentGroupCode, userId)
                                                }

                                                val addMemberResult = groupRepository.addMemberToGroup(newGroupCode, userId)
                                                if (currentGroupCode == newGroupCode) {
                                                    alreadyInGroupMessage = "You are already in this group"
                                                    groupCodeValidationErrorMessage = null
                                                } else {
                                                    alreadyInGroupMessage = null
                                                    if (addMemberResult) {
                                                        userRepository.updateUserGroupCode(userId, newGroupCode)
                                                        groupSwitchSuccessMessage = "Successfully switched groups!"
                                                    } else {
                                                        // HANDLE FAILURE
                                                        groupCodeValidationErrorMessage = "Failed to join the group. Please try again."
                                                    }
                                                }
                                            } else {
                                                // HANDLE FAILURE FOR NON EXISTENT GROUP CODE
                                                groupCodeValidationErrorMessage = "Group code not found. Please check the code and try again"
                                            }
                                        } ?: run {
                                            groupCodeValidationErrorMessage = "Error: User not identified"
                                        }

                                    } else {
                                        // HANDLE VALIDATION ERROR
                                        groupCodeValidationErrorMessage = "Group code cannot be blank."
                                    }

                                }
                            },
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
                            onClick = { showDeleteAccountDialog = true },
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = red_error,
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
                        DeleteAccountDialog(
                            showDialog = showDeleteAccountDialog,
                            onConfirm = { password -> settingsViewModel.deleteAccount(password) },
                            deleteAccountState = deleteAccountState,
                            onDismiss = {
                                settingsViewModel.resetDeleteAccountState()
                                showDeleteAccountDialog = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DeleteAccountDialog(
    showDialog: Boolean,
    onConfirm: (String) -> Unit,
    deleteAccountState: DeleteAccountResult?,
    onDismiss: () -> Unit
) {
    var showIncorrectPasswordError by remember { mutableStateOf(false) }
    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState == DeleteAccountResult.IncorrectPassword) {
            showIncorrectPasswordError = true
        } else if (deleteAccountState == DeleteAccountResult.Success) {

        }
    }
    if (showDialog) {
        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text(
                        text = "Deleting your account will permanently remove all your data. This cannot be undone. If you are sure, you can enter your password to confirm account deletion.",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                    )
                    TextEntryModule(
                        modifier = Modifier
                            .fillMaxWidth(),
                        description = "Password",
                        hint = "Enter password",
                        textValue = password,
                        textColor = Color.Gray,
                        cursorColor = purple_primary,
                        onValueChanged = { password = it },
                        trailingIcon = Icons.Default.RemoveRedEye,
                        onTrailingIconClick = {
                            passwordVisibility = !passwordVisibility
                        },
                        leadingIcon = Icons.Default.VpnKey,
                        visualTransformation = if (passwordVisibility) {
                            VisualTransformation.None
                        } else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                    // Show error message if delete account state indicates incorrect password
                    if (showIncorrectPasswordError) {
                        Text(
                            text = "Incorrect password. Please try again.",
                            color = red_error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(password)
                    },
                    shape = RoundedCornerShape(25.dp),
                    enabled = password.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showIncorrectPasswordError = false
                        onDismiss()
                              },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = light_purple,
                        contentColor = purple_primary
                    ),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text("Cancel")
                }
            }

        )
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        content()
    }
}