package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.ui.navigation.BottomNavScreen
import com.example.ui.navigation.Screen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavScreen.Calculator,
        BottomNavScreen.Invoices,
        BottomNavScreen.History,
        BottomNavScreen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Calculator.route) { CalculatorScreen() }
            composable(BottomNavScreen.Invoices.route) { 
                InvoiceCreateScreen(onNavigateToHistory = {
                    navController.navigate(BottomNavScreen.History.route) {
                        popUpTo(BottomNavScreen.Calculator.route) { saveState = false }
                    }
                })
            }
            composable(BottomNavScreen.History.route) { HistoryScreen() }
            composable(BottomNavScreen.Settings.route) { SettingsScreen(onLogout) }
        }
    }
}
