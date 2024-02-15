package org.housemate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.housemate.model.BottomNavItem
import org.housemate.userinterface.sharedcomponents.BottomNavigationBar


@Composable
fun HouseMateApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    BottomNavItem(
                        name = "Home",
                        route = "home",
                        icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                        name = "Chores",
                        route = "chores",
                        icon = Icons.AutoMirrored.Filled.Assignment
                    ),
                    BottomNavItem(
                        name = "Calendar",
                        route = "calendar",
                        icon = Icons.Default.CalendarToday
                    ),
                    BottomNavItem(
                        name = "Expenses",
                        route = "expenses",
                        icon = Icons.Default.AttachMoney
                    ),
                    BottomNavItem(
                        name = "Stats",
                        route = "stats",
                        icon = Icons.Default.Analytics
                    ),
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                })
        }
    ) { paddingValues ->
        Box(
            modifier = (Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
        ) {
            Navigation(navController = navController)
        }
    }
}
