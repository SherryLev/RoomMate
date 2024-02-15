package org.housemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import org.housemate.model.BottomNavItem
import org.housemate.userinterface.sharedcomponents.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        items = listOf(
                            BottomNavItem(
                                name = "Home",
                                route = "Home",
                                icon = Icons.Default.Home
                            ),
                            BottomNavItem(
                                name = "Chores",
                                route = "Chores",
                                icon = Icons.AutoMirrored.Filled.Assignment
                            ),
                            BottomNavItem(
                                name = "Calendar",
                                route = "Calendar",
                                icon = Icons.Default.CalendarToday
                            ),
                            BottomNavItem(
                                name = "Expenses",
                                route = "Expenses",
                                icon = Icons.Default.AttachMoney
                            ),
                            BottomNavItem(
                                name = "Stats",
                                route = "Stats",
                                icon = Icons.Default.Analytics
                            )
                        ),
                        navController = navController,
                        onItemClick = {
                        navController.navigate(it.route)
                    })
                },
            ) { paddingValues ->
                Box(
                    modifier = (Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
                ) {
                    Navigation(navController = navController)
                }
            }
        }
    }
}



