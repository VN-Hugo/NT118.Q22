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
    val internalNavController = rememberNavController()
    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        bottomBar = {
            // Chỉ hiển thị BottomBar ở các Tab chính (Dashboard, Hotels, Bookings, Analytics, Profile)
            val showBottomBar = currentRoute in listOf("dashboard", "hotels", "bookings", "analytics", "profile")
            if (showBottomBar) {
                HotelBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        internalNavController.navigate(targetRoute) {
                            popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
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
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") { OwnerDashboardScreen() }

            // Tab QUẢN LÝ KHÁCH SẠN
            composable("hotels") {
                HotelManagementScreen(
                    onAddHotelClick = { internalNavController.navigate("add_hotel") },
                    onEditHotelClick = { proId -> internalNavController.navigate("edit_hotel/$proId") },
                    onManageRoomsClick = { proId -> internalNavController.navigate("room_mgmt/$proId") }
                )
            }

            composable("add_hotel") {
                AddHotelScreen(
                    onBack = { internalNavController.popBackStack() },
                    onSuccess = { proId ->
                        // Tạo thông tin cơ bản xong -> Tự động chuyển sang thêm phòng
                        internalNavController.navigate("add_room/$proId") {
                            popUpTo("add_hotel") { inclusive = true }
                        }
                    }
                )
            }

            composable("edit_hotel/{proId}") {
                AddHotelScreen(
                    onBack = { internalNavController.popBackStack() },
                    onSuccess = { internalNavController.popBackStack() }
                )
            }

            // Tab QUẢN LÝ PHÒNG
            composable("room_mgmt/{proId}") { backStackEntry ->
                val proId = backStackEntry.arguments?.getString("proId") ?: ""
                RoomManagementScreen(
                    onBack = { internalNavController.popBackStack() },
                    onAddRoomClick = { internalNavController.navigate("add_room/$proId") },
                    onEditRoomClick = { roomTypeId ->
                        internalNavController.navigate("edit_room/$proId/$roomTypeId")
                    }
                )
            }

            composable("add_room/{proId}") {
                AddRoomScreen(onBack = { internalNavController.popBackStack() })
            }

            composable("edit_room/{proId}/{roomTypeId}") {
                AddRoomScreen(onBack = { internalNavController.popBackStack() })
            }

            // Tab QUẢN LÝ ĐƠN ĐẶT & LỊCH
            composable("bookings") {
                BookingManagementScreen(
                    onNavigateToCalendar = { internalNavController.navigate("room_calendar") }
                )
            }

            composable("room_calendar") {
                RoomCalendarScreen(onBack = { internalNavController.popBackStack() })
            }

            // Tab PHÂN TÍCH & ĐÁNH GIÁ
            composable("analytics") {
                ReviewScreen() // Đã fix: Không truyền tham số thừa
            }

            // Tab HỒ SƠ CÁ NHÂN
            composable("profile") {
                OwnerProfileScreen(onNavigate = { action ->
                    if (action == "logout") {
                        rootNavController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        internalNavController.navigate(action)
                    }
                })
            }
        }
    }
}