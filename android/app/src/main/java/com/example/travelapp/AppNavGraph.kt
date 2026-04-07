package com.example.travelapp

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelapp.ui.login.LoginScreen
import com.example.travelapp.ui.login.WelcomeScreen
import com.example.travelapp.ui.register.RegisterScreen
import com.example.travelapp.ui.dashboard.MainDashboardContainer
import com.example.travelapp.ui.owner.OwnerDashboardContainer

// Quản lý Route tập trung
object Routes {
    // Luồng ngoài (Auth)
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Luồng trong (Main app)
    const val MAIN_DASHBOARD = "main_dashboard"
    const val OWNER_DASHBOARD = "owner_dashboard"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    
    fun navigateByRole(role: String) {
        if (role == "HOTEL_OWNER") {
            navController.navigate(Routes.OWNER_DASHBOARD) {
                popUpTo(Routes.WELCOME) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.MAIN_DASHBOARD) {
                popUpTo(Routes.WELCOME) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        // 1. Màn hình Welcome
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = { role -> navigateByRole(role) }
            )
        }

        // 2. Màn hình Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = { role -> navigateByRole(role) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        // 3. Màn hình Register
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // 4. Cụm Dashboard cho Người dùng (Traveler)
        composable(Routes.MAIN_DASHBOARD) {
            MainDashboardContainer(rootNavController = navController)
        }

        // 5. Cụm Dashboard cho Chủ khách sạn (Owner)
        composable(Routes.OWNER_DASHBOARD) {
            OwnerDashboardContainer(rootNavController = navController)
        }
    }
}
