package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.travelapp.Routes
import com.example.travelapp.ui.components.TravelBottomBar
import com.example.travelapp.ui.planner.AIPlannerScreen

@Composable
fun MainDashboardContainer(rootNavController: NavHostController) {
    // NavController riêng cho các tab bên trong
    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            TravelBottomBar(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    internalNavController.navigate(targetRoute) {
                        // Tránh chồng chéo các màn hình khi nhấn tab nhiều lần
                        popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        // NavHost nội bộ xử lý các nút Home, Explore, Profile...
        NavHost(
            navController = internalNavController,
            startDestination = "home", // Mặc định vào tab home
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { SmartTravelHomeScreen() }

            composable("profile") {
                ProfileScreen(
                    onLogoutSuccess = {
                        // Dùng rootNavController để thoát ra màn hình Welcome bên ngoài
                        rootNavController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.MAIN_DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
            composable("ai_planner") {
                AIPlannerScreen()
            }
            composable("explore") {
                ExploreScreen()
            }
            composable("trips") {
                MyTripsScreen()
            }
        }
    }
}