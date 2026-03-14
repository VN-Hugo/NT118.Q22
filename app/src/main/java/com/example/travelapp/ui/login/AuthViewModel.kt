package com.example.travelapp.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.travelapp.data.User
import com.example.travelapp.repository.UserRepository

// Định nghĩa các trạng thái của quá trình xác thực
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = UserRepository()

    // Biến trạng thái để UI quan sát (Observer)
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    /**
     * Logic Đăng nhập
     */
    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            authState = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        authState = AuthState.Loading
        repository.loginUser(email, pass) { success ->
            if (success) {
                authState = AuthState.Success
            } else {
                authState = AuthState.Error("Email hoặc mật khẩu không đúng")
            }
        }
    }

    /**
     * Logic Đăng ký (Gồm tạo Auth và lưu Database)
     */
    fun signUp(email: String, pass: String, name: String) {
        authState = AuthState.Loading

        // 1. Đăng ký tài khoản hệ thống trước
        repository.registerUser(email, pass) { success, uid ->
            if (success && uid != null) {
                // 2. Nếu thành công thì lưu thông tin chi tiết vào Database
                saveUserToDatabase(uid, email, name)
            } else {
                authState = AuthState.Error("Đăng ký tài khoản thất bại")
            }
        }
    }

    /**
     * Hàm phụ: Lưu thông tin User sau khi đăng ký thành công
     */
    private fun saveUserToDatabase(uid: String, email: String, name: String) {
        val newUser = User(
            uid = uid,
            email = email,
            fullName = name,
            role = "USER",
            createdAt = System.currentTimeMillis()
        )

        repository.saveUser(newUser) { success ->
            if (success) {
                authState = AuthState.Success
            } else {
                authState = AuthState.Error("Lỗi khi lưu thông tin người dùng")
            }
        }
    }

    // Reset trạng thái về Idle (dùng khi chuyển màn hình hoặc đóng thông báo lỗi)
    fun resetState() {
        authState = AuthState.Idle
    }
}