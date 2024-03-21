package org.housemate.presentation.userinterface.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.BottomNavItem
import org.housemate.presentation.sharedcomponents.BottomNavigationBar
import org.housemate.utils.AppScreenRoutes
import org.housemate.utils.AuthScreen
import org.housemate.utils.HomeNavGraph
import org.housemate.utils.SettingsScreenRoutes

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            if (currentRoute(navController) != SettingsScreenRoutes.SettingsScreen.route &&
                currentRoute(navController) != AuthScreen.Login.route &&
                currentRoute(navController) != AuthScreen.Register.route) {
                BottomNavigationBar(
                    items = listOf(
                        BottomNavItem(
                            name = "Home",
                            route = AppScreenRoutes.HomeScreen.route,
                            icon = Icons.Default.Home
                        ),
                        BottomNavItem(
                            name = "Chores",
                            route = AppScreenRoutes.ChoresScreen.route,
                            icon = Icons.AutoMirrored.Filled.Assignment
                        ),
                        BottomNavItem(
                            name = "Calendar",
                            route = AppScreenRoutes.CalendarScreen.route,
                            icon = Icons.Default.CalendarToday
                        ),
                        BottomNavItem(
                            name = "Expenses",
                            route = AppScreenRoutes.ExpensesScreen.route,
                            icon = Icons.Default.AttachMoney
                        ),
                        BottomNavItem(
                            name = "Stats",
                            route = AppScreenRoutes.StatsScreen.route,
                            icon = Icons.Default.Analytics
                        ),
                    ),
                    navController = navController,
                    onItemClick = {
                        navController.navigate(it.route)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = (Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
        ) {
            HomeNavGraph(navController = navController)
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}