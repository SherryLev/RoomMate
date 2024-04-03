package org.housemate.utils

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.firebase.firestore.FirebaseFirestore
import org.housemate.presentation.userinterface.authentication.LoginScreen
import org.housemate.presentation.userinterface.authentication.RegisterScreen
import org.housemate.presentation.userinterface.authentication.SetupScreen
import org.housemate.data.firestore.GroupRepositoryImpl
import org.housemate.data.firestore.UserRepositoryImpl
import org.housemate.presentation.userinterface.authentication.SuccessScreen


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route){
            BackHandler(true) {
                // Or do nothing
                Log.i("LOG_TAG", "Clicked back")
            }
            LoginScreen(
                onLoginSuccessNavigation = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(AuthScreen.Register.route){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(AuthScreen.Register.route){
            RegisterScreen(
                onRegisterSuccessNavigation = {
                    navController.popBackStack()
                    navController.navigate(AuthScreen.SetupScreen.route)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(AuthScreen.Login.route){
                        popUpTo(0)
                    }
                }
            )
        }
        composable(AuthScreen.SetupScreen.route){
            SetupScreen(
                onNavigateToHomeScreen = {
                    navController.navigate(Graph.HOME)
                },
                groupRepository = GroupRepositoryImpl(),
                userRepository = UserRepositoryImpl(FirebaseFirestore.getInstance())
            )
        }

        composable(
            route = "success/{groupcode}",
            arguments = listOf(navArgument("groupCode") { type = NavType.StringType})
        ) { backStackEntry ->
            SuccessScreen(groupCode = backStackEntry.arguments?.getString("groupCode") ?: "")
        }
    }
}

sealed class AuthScreen(val route:String){
    object Login:AuthScreen("login_screen")
    object Register:AuthScreen("register_screen")
    object SetupScreen:AuthScreen("setup_screen")
}
