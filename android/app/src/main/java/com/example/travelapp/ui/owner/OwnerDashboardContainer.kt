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
                AddRoomScreen(
                    onBack = { internalNavController.popBackStack() }
                )
            }

            composable("edit_room/{proId}/{roomTypeId}") {
                AddRoomScreen(
                    onBack = { internalNavController.popBackStack() }
                )
            }

            composable("bookings") { 
                BookingManagementScreen(
                    onNavigateToCalendar = { internalNavController.navigate("room_calendar") }
                ) 
            }

            composable("room_calendar") {
                RoomCalendarScreen(
                    onBack = { internalNavController.popBackStack() }
                )
            }

            composable("analytics") { ReviewScreen(onNavigate = { internalNavController.navigate(it) }) }
            composable("profile") {
                OwnerProfileScreen(onNavigate = {
                    if (it == "logout") rootNavController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.MAIN_DASHBOARD) { inclusive = true }
                    }
                    else internalNavController.navigate(it)
                })
            }
        }
    }
}
