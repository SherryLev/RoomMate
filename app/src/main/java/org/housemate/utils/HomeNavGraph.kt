package org.housemate.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.housemate.presentation.userinterface.calendar.CalendarScreen
import org.housemate.presentation.userinterface.chores.ChoresScreen
import org.housemate.presentation.userinterface.expenses.ExpensesScreen
import org.housemate.presentation.userinterface.home.EditUserInfoScreen
import org.housemate.presentation.userinterface.home.HomeScreenHelper
import org.housemate.presentation.userinterface.home.SettingsScreen
import org.housemate.presentation.userinterface.authentication.SetupUsernameScreen
import org.housemate.presentation.userinterface.stats.StatsScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = AppScreenRoutes.HomeScreen.route
    ) {
        composable(AppScreenRoutes.HomeScreen.route){
            HomeScreenHelper(
                onNavigateToSettingsScreen = {
                    navController.navigate(Graph.SETTINGS){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(AppScreenRoutes.ChoresScreen.route){
            ChoresScreen()
        }
        composable(AppScreenRoutes.CalendarScreen.route){
            CalendarScreen()
        }
        composable(AppScreenRoutes.ExpensesScreen.route){
            ExpensesScreen()
        }
        composable(AppScreenRoutes.StatsScreen.route){
            StatsScreen()
        }
        settingsNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SETTINGS,
        startDestination = SettingsScreenRoutes.SettingsScreen.route
    ) {
        composable(route = SettingsScreenRoutes.SettingsScreen.route) {
            SettingsScreen(navController)
        }
        composable(route = SettingsScreenRoutes.EditUserInfoScreen.route) {
            EditUserInfoScreen()
        }
    }
}



sealed class SettingsScreenRoutes(val route: String) {
    object SettingsScreen : SettingsScreenRoutes(route = "settings_screen")
    object EditUserInfoScreen : SettingsScreenRoutes(route = "user_info_screen")
}

sealed class AppScreenRoutes(val route:String){
    object HomeScreen: AppScreenRoutes("home_screen")
    object ChoresScreen: AppScreenRoutes("chores_screen")
    object CalendarScreen: AppScreenRoutes("calendar_screen")
    object ExpensesScreen: AppScreenRoutes("expenses_screen")
    object StatsScreen: AppScreenRoutes("stats_screen")
}