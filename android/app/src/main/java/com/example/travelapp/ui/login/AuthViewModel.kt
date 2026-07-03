package com.example.travelapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import com.example.travelapp.data.model.User
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
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
            val success = userRepository.loginUser(email, pass)
            if (success) {
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

                // KIỂM TRA XEM ĐÃ XÁC NHẬN EMAIL CHƯA (Cho phép đăng nhập thẳng ở chế độ Debug)
                if (currentUser != null && (currentUser.isEmailVerified || com.example.travelapp.BuildConfig.DEBUG)) {
                    fetchRoleAndSuccess()
                } else {
                    authState = AuthState.Error("Tài khoản chưa xác thực. Vui lòng kiểm tra Email của bạn!")
                     userRepository.logout()
                }
            } else {
                authState = AuthState.Error("Email hoặc mật khẩu không đúng")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            val success = userRepository.signInWithGoogle(idToken)
            if (success) {
                fetchRoleAndSuccess()
            } else {
                authState = AuthState.Error("Đăng nhập Google thất bại")
            }
        }
    }

    private suspend fun fetchRoleAndSuccess() {
        val uid = userRepository.getCurrentUserId()
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
            try {
                // 1. Đăng ký tài khoản trên Firebase Auth
                val uid = userRepository.registerUser(email, pass)

                if (uid != null) {
                    // 2. Tạo đối tượng User để lưu vào Firestore
                    val user = User(
                        uid = uid,
                        fullName = name,
                        email = email,
                        role = role
                    )
                    // 3. Lưu thông tin User
                    val saved = userRepository.saveUser(user)
                    if (saved) {
                        authState = AuthState.Success(role)
                    } else {
                        authState = AuthState.Error("Lưu thông tin người dùng thất bại")
                    }
                } else {
                    authState = AuthState.Error("Đăng ký tài khoản thất bại (Không lấy được ID)")
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                // BẮT ĐƯỢC LỖI TRÙNG EMAIL Ở ĐÂY
                authState = AuthState.Error("Email này đã được đăng ký! Vui lòng dùng email khác.")
            } catch (e: FirebaseAuthWeakPasswordException) {
                authState = AuthState.Error("Mật khẩu quá yếu (cần ít nhất 6 ký tự).")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                authState = AuthState.Error("Định dạng email không hợp lệ!")
            } catch (e: Exception) {
                // Các lỗi khác (như rớt mạng...)
                authState = AuthState.Error(e.message ?: "Lỗi không xác định khi đăng ký")
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            authState = AuthState.Error("Vui lòng nhập email để reset mật khẩu.")
            return
        }
        viewModelScope.launch {
            authState = AuthState.Loading
            val result = userRepository.resetPassword(email)
            result.fold(
                onSuccess = {
                    authState = AuthState.Error("Email khôi phục đã được gửi. Vui lòng kiểm tra hộp thư!") // Dùng Error tạm để mượn Toast báo thành công, hoặc bạn tạo state riêng
                },
                onFailure = { e ->
                    authState = AuthState.Error(e.message ?: "Lỗi gửi email")
                }
            )
        }
    }
}