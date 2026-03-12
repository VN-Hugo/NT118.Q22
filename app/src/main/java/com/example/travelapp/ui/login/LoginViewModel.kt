package com.example.travelapp.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // 1. Quản lý trạng thái Loading
    var isLoading = mutableStateOf(false)
        private set // Chỉ cho phép ViewModel sửa, bên ngoài chỉ được đọc

    // 2. Hàm xử lý khi nhấn nút Đăng nhập bằng Google/Apple
    fun onSocialLoginClick(provider: String) {
        // Bật trạng thái loading
        isLoading.value = true

        // Giả lập xử lý đăng nhập (sau này bạn sẽ gọi Firebase ở đây)
        viewModelScope.launch {
            try {
                // Giả sử chờ Firebase phản hồi trong 2 giây
                kotlinx.coroutines.delay(2000)

                println("Đăng nhập thành công với $provider")
            } catch (e: Exception) {
                println("Lỗi: ${e.message}")
            } finally {
                // Tắt loading
                isLoading.value = false
            }
        }
    }
}