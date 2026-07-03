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
            // Chỉ hiển thị BottomBar cho các tab chính để tránh rối UI
            val showBottomBar = currentRoute in listOf("home", "explore", "trips", "ai_planner", "profile")
            if (showBottomBar) {
                TravelBottomBar(
                    currentRoute = currentRoute, // ĐÃ FIX TÊN THAM SỐ
                    onNavigate = { targetRoute ->
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
        }
    ) { paddingValues ->
        NavHost(
            navController = internalNavController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. Tab Trang chủ
            composable("home") { 
                SmartTravelHomeScreen(
                    onPropertyClick = { proId ->
                        rootNavController.navigate("property_detail/$proId")
                    }
                ) 
            }

            // 2. Tab Khám phá
            composable("explore") {
                ExploreScreen(
                    onPropertyClick = { proId ->
                        rootNavController.navigate("property_detail/$proId")
                    }
                )
            }

            // 3. Tab Chuyến đi của tôi
            composable("trips") {
                MyTripsScreen(
                    onBookingClick = { proId ->
                        // Điều hướng đến chi tiết khách sạn từ đơn đặt phòng
                        rootNavController.navigate("property_detail/$proId")
                    },
                    onPropertyClick = { proId ->
                        rootNavController.navigate("property_detail/$proId")
                    }
                )
            }

            // 4. Tab Trợ lý AI Planner
            composable("ai_planner") {
                AIPlannerScreen(
                    onPropertyClick = { proId ->
                        rootNavController.navigate("property_detail/$proId")
                    }
                )
            }

            // 5. Tab Hồ sơ cá nhân
            composable("profile") {
                ProfileScreen(
                    onLogoutSuccess = {
                        rootNavController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToWishlist = {
                        internalNavController.navigate("wishlist")
                    }
                )
            }

            // 6. Màn hình Danh sách yêu thích (Nằm trong luồng Dashboard)
            composable("wishlist") {
                WishlistScreen(
                    onBack = { internalNavController.popBackStack() },
                    onPropertyClick = { proId ->
                        rootNavController.navigate("property_detail/$proId")
                    }
                )
            }
        }
    }
}
