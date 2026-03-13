package com.example.travelapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelapp.ui.login.LoginScreen
import com.example.travelapp.ui.login.WelcomeScreen

// 1. Quản lý Route bằng object để tránh gõ sai chữ
object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val HOME = "home"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        // Màn hình Welcome
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        // Màn hình Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    // Xóa toàn bộ lịch sử trước đó để người dùng không bấm back quay lại màn Login được
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // Định nghĩa màn Home (tạm thời) để không bị crash khi điều hướng
        composable(Routes.HOME) {
            // Thay bằng HomeScreen() của bạn sau này
        }
    }
}