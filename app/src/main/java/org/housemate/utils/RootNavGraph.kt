package org.housemate.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.R
import org.housemate.data.AuthRepositoryImpl
import org.housemate.data.GroupRepositoryImpl
import org.housemate.data.UserRepositoryImpl
import org.housemate.domain.repositories.AuthRepository
import org.housemate.domain.repositories.UserRepository
import org.housemate.presentation.userinterface.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {

    val firestore = FirebaseFirestore.getInstance()

    val authRepository: AuthRepository
    val userRepository: UserRepository

    userRepository = UserRepositoryImpl(firestore)

    authRepository = AuthRepositoryImpl(
        UserRepositoryImpl(firestore),  // Replace with actual implementation
        GroupRepositoryImpl(userRepository),
        firestore,
        FirebaseAuth.getInstance()
    )

    // Remember the logged-in state
    val loggedInState = remember { mutableStateOf<Boolean?>(null) }

    // Use LaunchedEffect to trigger the login state check
    LaunchedEffect(true) {
        // Check the login state and update the logged-in state
        loggedInState.value = authRepository.getLoginState()
    }

    val startDestination = when {
        loggedInState.value == true -> Graph.HOME
        else -> Graph.AUTHENTICATION
    }

    if (loggedInState.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.housemate_logo_foreground), // Replace with your splash screen resource
                    contentDescription = "Splash Screen", // Provide a content description
                    modifier = Modifier.size(200.dp) // Adjust size as needed
                )
                CircularProgressIndicator(
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    } else {
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = startDestination
        ) {
            authNavGraph(navController = navController)
            composable(route = Graph.HOME) {
                HomeScreen()
            }
        }
    }
}


object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val SETTINGS = "settings_graph"
}