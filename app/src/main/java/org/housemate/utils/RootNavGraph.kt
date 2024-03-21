package org.housemate.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.data.AuthRepositoryImpl
import org.housemate.data.firestore.UserRepositoryImpl
import org.housemate.domain.repositories.AuthRepository
import org.housemate.presentation.userinterface.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {

    val firestore = FirebaseFirestore.getInstance()

    val authRepository: AuthRepository

    authRepository = AuthRepositoryImpl(
        UserRepositoryImpl(firestore),  // Replace with actual implementation
        firestore,
        FirebaseAuth.getInstance()
    )

    // Remember the logged-in state
    val loggedInState = remember { mutableStateOf(false) }

    // Use LaunchedEffect to trigger the login state check
    LaunchedEffect(true) {
        // Check the login state and update the logged-in state
        loggedInState.value = authRepository.getLoginState()
    }

    val startDestination = if (loggedInState.value) Graph.HOME else Graph.AUTHENTICATION

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


object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val SETTINGS = "settings_graph"
}