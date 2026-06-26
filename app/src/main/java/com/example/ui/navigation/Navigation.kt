package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
}

sealed class BottomNavScreen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Calculator : BottomNavScreen("calculator", "Calculator", Icons.Default.Edit)
    object Invoices : BottomNavScreen("invoices", "Invoices", Icons.Default.List)
    object History : BottomNavScreen("history", "History", Icons.Default.DateRange)
    object Settings : BottomNavScreen("settings", "Settings", Icons.Default.Settings)
}
