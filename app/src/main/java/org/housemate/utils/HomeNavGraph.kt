package org.housemate.utils

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.housemate.presentation.userinterface.chores.ChoresScreen
import org.housemate.presentation.userinterface.expenses.AddExpenseScreen
import org.housemate.presentation.userinterface.expenses.ExpensesScreen
import org.housemate.presentation.userinterface.expenses.SettleUpScreen
import org.housemate.presentation.userinterface.home.EditUserInfoScreen
import org.housemate.presentation.userinterface.home.HomeScreenHelper
import org.housemate.presentation.userinterface.home.SettingsScreen
import org.housemate.presentation.userinterface.stats.StatsScreen
import org.housemate.presentation.viewmodel.ExpenseViewModel

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
        composable(AppScreenRoutes.ExpensesScreen.route){
                backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppScreenRoutes.ExpensesScreen.route)
            }
            val parentViewModel: ExpenseViewModel = hiltViewModel(parentEntry)
            ExpensesScreen(navController, parentViewModel)
        }
        composable(AppScreenRoutes.AddExpenseScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppScreenRoutes.ExpensesScreen.route)
            }
            val parentViewModel: ExpenseViewModel = hiltViewModel(parentEntry)
            AddExpenseScreen(navController, parentViewModel)
        }
        composable(AppScreenRoutes.SettleUpScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppScreenRoutes.ExpensesScreen.route)
            }
            val parentViewModel: ExpenseViewModel = hiltViewModel(parentEntry)
            SettleUpScreen(navController, parentViewModel)
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
            SettingsScreen(
                navController = navController,
                onNavigateToAuthScreen = {
                navController.navigate(Graph.AUTHENTICATION){
                    popUpTo(Graph.AUTHENTICATION) {
                        inclusive = true
                    }
                }
            })
        }
        composable(route = SettingsScreenRoutes.EditUserInfoScreen.route) {
            EditUserInfoScreen()
        }
    }
    authNavGraph(navController = navController)
}



sealed class SettingsScreenRoutes(val route: String) {
    object SettingsScreen : SettingsScreenRoutes(route = "settings_screen")
    object EditUserInfoScreen : SettingsScreenRoutes(route = "user_info_screen")
}

sealed class AppScreenRoutes(val route:String){
    object HomeScreen: AppScreenRoutes("home_screen")
    object ChoresScreen: AppScreenRoutes("chores_screen")
    object ExpensesScreen: AppScreenRoutes("expenses_screen")
    object AddExpenseScreen: AppScreenRoutes("add_expense_screen")

    object SettleUpScreen: AppScreenRoutes("settle_up_screen")
    object StatsScreen: AppScreenRoutes("stats_screen")
}