package org.housemate

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.housemate.userinterface.authentication.LoginScreen
import org.housemate.userinterface.authentication.SignUpScreen
import org.housemate.userinterface.chores.MainLayout
import org.housemate.userinterface.home.Home


@Composable
fun HouseMateApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login.route) {
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Routes.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(Routes.Home.route) {
            Home(navController = navController)
        }
        composable(Routes.Chores.route) {
            MainLayout(navController = navController)
        }
    }
}
