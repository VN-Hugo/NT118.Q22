package com.example.travelapp.ui.dashboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.User
import com.example.travelapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState = _profileState.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = userRepository.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                _profileState.value = ProfileState.Loading
                val user = userRepository.getUserProfile(uid)
                if (user != null) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("Không tìm thấy thông tin người dùng")
                }
            }
        } else {
            _profileState.value = ProfileState.Error("Người dùng chưa đăng nhập")
        }
    }

    fun uploadAvatar(uri: Uri) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _isUploading.value = true
            val downloadUrl = userRepository.uploadAvatar(uid, uri)
            if (downloadUrl != null) {
                val success = userRepository.updateProfile(uid, mapOf("avatarUrl" to downloadUrl))
                if (success) {
                    loadUserProfile() // Refresh data
                }
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