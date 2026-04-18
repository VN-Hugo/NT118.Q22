package com.example.travelapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import com.example.travelapp.domain.usecase.LoginUseCase
import com.example.travelapp.domain.usecase.RegisterUseCase
import com.example.travelapp.domain.usecase.SignInWithGoogleUseCase
import com.example.travelapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            authState = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }
        authState = AuthState.Loading
        viewModelScope.launch {
            val success = loginUseCase(email, pass)
            if (success) {
                fetchRoleAndSuccess()
            } else {
                authState = AuthState.Error("Email hoặc mật khẩu không đúng")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            val success = signInWithGoogleUseCase(idToken)
            if (success) {
                fetchRoleAndSuccess()
            } else {
                authState = AuthState.Error("Đăng nhập Google thất bại")
            }
        }
    }

    private suspend fun fetchRoleAndSuccess() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val user = userRepository.getUserProfile(uid)
            authState = AuthState.Success(user?.role ?: "USER")
        } else {
            authState = AuthState.Error("Không tìm thấy thông tin phiên đăng nhập")
        }
    }

    fun signUp(email: String, pass: String, name: String, role: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            val success = registerUseCase(email, pass, name, role)
            if (success) {
                authState = AuthState.Success(role)
            } else {
                authState = AuthState.Error("Đăng ký thất bại")
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}
