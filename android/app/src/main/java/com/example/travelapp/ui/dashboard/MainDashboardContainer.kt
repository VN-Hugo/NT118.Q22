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
    // NavController riêng cho các tab bên trong (Home, Explore, Trips, AI Planner, Profile)
    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            TravelBottomBar(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    // Tránh tạo nhiều instance của cùng một màn hình khi nhấn tab liên tục
                    internalNavController.navigate(targetRoute) {
                        popUpTo(internalNavController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        // NavHost nội bộ xử lý việc chuyển đổi giữa các Tab
        NavHost(
            navController = internalNavController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. Tab Trang chủ
            composable("home") { 
                SmartTravelHomeScreen(
                    onPropertyClick = { proId ->
                        // Sử dụng rootNavController để chuyển sang màn hình Chi tiết (ẩn Bottom Bar)
                        rootNavController.navigate("property_detail/$proId")
                    }
                ) 
            }

            // 2. Tab Khám phá
            composable("explore") {
                ExploreScreen(
                    onPropertyClick = { proId ->
                        // Sử dụng rootNavController để chuyển sang màn hình Chi tiết (ẩn Bottom Bar)
                        rootNavController.navigate("property_detail/$proId")
                    }
                )
            }

            // 3. Tab Chuyến đi của tôi
            composable("trips") {
                MyTripsScreen()
            }

            // 4. Tab Trợ lý AI Planner
            composable("ai_planner") {
                AIPlannerScreen()
            }

            // 5. Tab Hồ sơ cá nhân
            composable("profile") {
                ProfileScreen(
                    onLogoutSuccess = {
                        // Khi đăng xuất, dùng rootNavController quay về Welcome và xóa toàn bộ stack
                        rootNavController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.MAIN_DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
