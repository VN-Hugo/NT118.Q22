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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
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
            authState = if (success) {
                AuthState.Success
            } else {
                AuthState.Error("Email hoặc mật khẩu không đúng")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            val success = signInWithGoogleUseCase(idToken)
            authState = if (success) {
                AuthState.Success
            } else {
                AuthState.Error("Đăng nhập Google thất bại")
            }
        }
    }

    fun signUp(email: String, pass: String, name: String) {
        authState = AuthState.Loading

        viewModelScope.launch {
            val success = registerUseCase(email, pass, name)

            authState = if (success) {
                AuthState.Success
            } else {
                AuthState.Error("Đăng ký thất bại")
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }

}