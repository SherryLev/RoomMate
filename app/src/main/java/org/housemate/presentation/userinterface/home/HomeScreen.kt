package org.housemate.presentation.userinterface.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.housemate.domain.model.BottomNavItem
import org.housemate.presentation.sharedcomponents.BottomNavigationBar
import org.housemate.utils.AppScreenRoutes
import org.housemate.utils.AuthScreen
import org.housemate.utils.HomeNavGraph
import org.housemate.utils.SettingsScreenRoutes
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.housemate.domain.model.Chore
import org.housemate.presentation.userinterface.chores.TaskDisplayHouse
import org.housemate.presentation.userinterface.chores.TaskItem
import org.housemate.theme.green
import org.housemate.theme.md_theme_light_error
import java.math.BigDecimal


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