package com.example.travelapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelapp.ui.login.LoginScreen
import com.example.travelapp.ui.login.WelcomeScreen
import com.example.travelapp.ui.register.RegisterScreen
import com.example.travelapp.ui.dashboard.MainDashboardContainer

// Quản lý Route tập trung
object Routes {
    // Luồng ngoài (Auth)
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Luồng trong (Main app)
    const val MAIN_DASHBOARD = "main_dashboard"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        // 1. Màn hình Welcome
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    // Khi login bằng Google thành công từ Welcome
                    navController.navigate(Routes.MAIN_DASHBOARD) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // 2. Màn hình Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Khi login thành công, nhảy vào Dashboard và xóa sạch lịch sử Auth
                    navController.navigate(Routes.MAIN_DASHBOARD) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
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

        // 4. Cụm Dashboard (Chứa Bottom Bar và các tab Home, Profile...)
        composable(Routes.MAIN_DASHBOARD) {
            // Chúng ta truyền navController gốc vào để Dashboard có thể gọi lệnh Logout
            MainDashboardContainer(rootNavController = navController)
        }
    }
}