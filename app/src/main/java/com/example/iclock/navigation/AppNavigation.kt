package com.example.iclock.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iclock.screens.AlertDetailScreen
import com.example.iclock.screens.HomeScreen
import com.example.iclock.screens.NotificationsScreen
import com.example.iclock.screens.ProjectionsScreen

sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Projections : Screen("projections")
    object Notifications : Screen("notifications")
    object AlertDetail  : Screen("alert_detail")
}

@Composable
fun AppNavigation(startDestination: String = "home") {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Projections.route) {
            ProjectionsScreen(navController = navController)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        composable(Screen.AlertDetail.route) {
            AlertDetailScreen(navController = navController)
        }
    }
}