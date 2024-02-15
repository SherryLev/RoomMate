package org.housemate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.housemate.userinterface.authentication.LoginScreen
import org.housemate.userinterface.authentication.SignUpScreen
import org.housemate.userinterface.calendar.CalendarScreen
import org.housemate.userinterface.chores.MainLayout
import org.housemate.userinterface.expenses.ExpensesScreen
import org.housemate.userinterface.home.HomeScreen
import org.housemate.userinterface.stats.StatsScreen
import org.housemate.viewmodels.AuthViewModel


@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "auth") {
        navigation(
            startDestination = "login",
            route = "auth"
        ) {
            composable("login") {
                val viewModel = it.sharedViewModel<AuthViewModel>(navController)
                LoginScreen(navController = navController)
            }
            composable("signup") {
                val viewModel = it.sharedViewModel<AuthViewModel>(navController)
                SignUpScreen(navController = navController)
            }
        }

        navigation(
            startDestination = "home",
            route = "mainapp"
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("chores") {
                MainLayout(navController = navController)
            }
            composable("calendar") {
                CalendarScreen(navController = navController)
            }
            composable("expenses") {
                ExpensesScreen(navController = navController)
            }
            composable("stats") {
                StatsScreen(navController = navController)
            }

        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}