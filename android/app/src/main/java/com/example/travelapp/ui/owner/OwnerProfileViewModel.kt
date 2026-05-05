package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.User
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OwnerProfileState {
    object Loading : OwnerProfileState()
    data class Success(val user: User) : OwnerProfileState()
    data class Error(val message: String) : OwnerProfileState()
}

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<OwnerProfileState>(OwnerProfileState.Loading)
    val profileState = _profileState.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    // Trạng thái cho việc chỉnh sửa bằng TextFieldValue để hỗ trợ tiếng Việt mượt mà
    var editFullName by mutableStateOf(TextFieldValue(""))
    var editPhoneNumber by mutableStateOf(TextFieldValue(""))

    init {
        fetchUser()
    }

    private fun fetchUser() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            // Lắng nghe dữ liệu thời gian thực từ Firestore
            userRepository.getUserFlow(uid).collect { user ->
                if (user != null) {
                    _profileState.value = OwnerProfileState.Success(user)
                    // Chỉ nạp giá trị từ DB nếu các ô nhập đang trống để tránh đè khi user đang gõ
                    if (editFullName.text.isEmpty()) editFullName = TextFieldValue(user.fullName)
                    if (editPhoneNumber.text.isEmpty()) editPhoneNumber = TextFieldValue(user.phoneNumber)
                } else {
                    _profileState.value = OwnerProfileState.Error("Không tìm thấy thông tin tài khoản")
                }
            }
        }
    }

    fun updateProfile() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isUploading.value = true
            val updates = mapOf(
                "fullName" to editFullName.text,
                "phoneNumber" to editPhoneNumber.text
            )
            userRepository.updateProfile(uid, updates)
            _isUploading.value = false
        }
    }

    fun uploadAvatar(uri: Uri) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isUploading.value = true
            val url = userRepository.uploadAvatar(uid, uri)
            if (url != null) {
                userRepository.updateProfile(uid, mapOf("avatarUrl" to url))
            }
            _isUploading.value = false
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLoggedOut()
        }
    }
}
