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
            // Chỉ hiển thị BottomBar ở các màn hình chính (Tab)
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

            // Tab 2: Quản lý danh sách khách sạn
            composable("hotels") {
                HotelManagementScreen(
                    onAddHotelClick = { internalNavController.navigate("add_hotel") },
                    onManageRoomsClick = { proId -> internalNavController.navigate("room_mgmt/$proId") }
                )
            }

            // Màn hình Thêm khách sạn (Fullscreen)
            composable("add_hotel") {
                AddHotelScreen(
                    onBack = { internalNavController.popBackStack() },
                    onSuccess = { internalNavController.popBackStack() }
                )
            }

            // Màn hình Quản lý phòng của từng khách sạn
            composable("room_mgmt/{proId}") {
                RoomManagementScreen() // Sau này sẽ truyền proId vào ViewModel của màn hình này
            }

            composable("bookings") { BookingManagementScreen() }
            composable("analytics") { ReviewScreen(onNavigate = { internalNavController.navigate(it) }) }
            composable("profile") {
                OwnerProfileScreen(onNavigate = { 
                    if (it == "logout") rootNavController.navigate(Routes.WELCOME) { popUpTo(0) }
                    else internalNavController.navigate(it)
                })
            }
        }
    }
}
