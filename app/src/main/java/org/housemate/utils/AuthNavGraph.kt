package org.housemate.utils

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.housemate.presentation.userinterface.authentication.LoginScreen
import org.housemate.presentation.userinterface.authentication.RegisterScreen
import org.housemate.presentation.userinterface.authentication.SetupUsernameScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route){
            LoginScreen(
                onLoginSuccessNavigation = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(AuthScreen.Register.route){
                        popUpTo(0)
                    }
                },
                onLoginFailureNavigation = {
                    navController.navigate(AuthScreen.Login.route){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(AuthScreen.Register.route){
            RegisterScreen(
                onRegisterSuccessNavigation = {
                    navController.popBackStack()
                    navController.navigate(AuthScreen.SetupUsername.route)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(AuthScreen.Login.route){
                        popUpTo(0)
                    }
                },
                onRegisterFailureNavigation = {
                    navController.navigate(AuthScreen.Register.route){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(AuthScreen.SetupUsername.route){
            SetupUsernameScreen()
        }
    }
}

sealed class AuthScreen(val route:String){
    object Login:AuthScreen("login_screen")
    object Register:AuthScreen("register_screen")
    object SetupUsername:AuthScreen("setup_username_screen")
    object SetupGroupScreen: AppScreenRoutes("setup_group_screen")
}
