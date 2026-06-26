package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.ui.navigation.BottomNavScreen
import com.example.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

data class AppConfig(
    val maintenanceMode: Boolean = false,
    val bannerMessage: String = "",
    val enableHistory: Boolean = true
)

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    var appConfig by remember { mutableStateOf(AppConfig()) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("appConfig").document("settings")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    appConfig = AppConfig(
                        maintenanceMode = snapshot.getBoolean("maintenanceMode") ?: false,
                        bannerMessage = snapshot.getString("bannerMessage") ?: "",
                        enableHistory = snapshot.getBoolean("enableHistory") ?: true
                    )
                }
            }
    }

    if (appConfig.maintenanceMode) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Maintenance Mode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("The app is currently down for maintenance. Please check back later.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
        return
    }

    val items = mutableListOf(
        BottomNavScreen.Calculator,
        BottomNavScreen.Invoices
    )
    if (appConfig.enableHistory) {
        items.add(BottomNavScreen.History)
    }
    items.add(BottomNavScreen.Settings)

    Scaffold(
        topBar = {
            if (appConfig.bannerMessage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        appConfig.bannerMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
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
