package com.example.travelapp.ui.owner

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
import com.example.travelapp.ui.components.HotelBottomBar

@Composable
fun OwnerDashboardContainer(rootNavController: NavHostController) {
    // NavController riêng cho các tab của Owner
    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        bottomBar = {
            HotelBottomBar(
                currentRoute = currentRoute,
                onNavigate = { targetRoute ->
                    internalNavController.navigate(targetRoute) {
                        // Tránh tạo nhiều instance của cùng một màn hình khi nhấn tab liên tục
                        popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        // NavHost nội bộ cho luồng quản lý của Owner
        NavHost(
            navController = internalNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Tab 1: Tổng quan (Dashboard)
            composable("dashboard") {
                OwnerDashboardScreen()
            }

            // Tab 2: Quản lý khách sạn & Phòng
            composable("hotels") {
                RoomManagementScreen()
            }

            // Tab 3: Quản lý đặt phòng
            composable("bookings") {
                BookingManagementScreen()
            }

            // Tab 4: Phân tích & Đánh giá (Analytics/Reviews)
            composable("analytics") {
                ReviewScreen(onNavigate = { route -> internalNavController.navigate(route) })
            }

            // Tab 5: Hồ sơ chủ sở hữu
            composable("profile") {
                OwnerProfileScreen(
                    onNavigate = { route ->
                        if (route == "logout") {
                            // Logic logout thực tế nên được gọi ở đây hoặc trong ViewModel
                            rootNavController.navigate(Routes.WELCOME) {
                                popUpTo(Routes.MAIN_DASHBOARD) { inclusive = true }
                            }
                        } else {
                            internalNavController.navigate(route)
                        }
                    }
                )
            }

            // Màn hình chi tiết đặt phòng (Navigation nội bộ)
            composable("booking_detail") {
                BookingDetailScreen(onNavigate = { internalNavController.popBackStack() })
            }
        }
    }
}
