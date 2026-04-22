package com.example.travelapp.ui.dashboard

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.travelapp.data.model.User
import com.example.travelapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
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

    var editFullName by mutableStateOf(TextFieldValue(""))
    var editPhoneNumber by mutableStateOf(TextFieldValue(""))

    init {
        fetchUser(isInitial = true)
    }

    private fun fetchUser(isInitial: Boolean) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid ?: return
        
        viewModelScope.launch {
            if (isInitial) _profileState.value = ProfileState.Loading
            
            // 1. Cố gắng lấy từ Firestore
            var user = userRepository.getUserProfile(uid)
            
            // 2. Nếu Firestore chưa có (user mới), dùng dữ liệu từ Auth
            if (user == null && currentUser != null) {
                user = User(
                    uid = uid,
                    fullName = currentUser.displayName ?: "",
                    email = currentUser.email ?: "",
                    avatarUrl = currentUser.photoUrl?.toString() ?: ""
                )
                // Lưu luôn vào Firestore để lần sau không bị null nữa
                userRepository.saveUser(user)
            }

            if (user != null) {
                _profileState.value = ProfileState.Success(user)
                // Khởi tạo các ô nhập liệu
                if (editFullName.text.isEmpty()) editFullName = TextFieldValue(user.fullName)
                if (editPhoneNumber.text.isEmpty()) editPhoneNumber = TextFieldValue(user.phoneNumber)
            } else {
                _profileState.value = ProfileState.Error("Không thể xác định thông tin người dùng")
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        val uid = userRepository.getCurrentUserId() ?: return
        _isUploading.value = true

        MediaManager.get().upload(uri)
            .option("upload_preset", "travel_app_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String
                    if (imageUrl != null) {
                        updateAvatarInDatabase(uid, imageUrl)
                    } else {
                        _isUploading.value = false
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    _isUploading.value = false
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    private fun updateAvatarInDatabase(uid: String, imageUrl: String) {
        viewModelScope.launch {
            val success = userRepository.updateProfile(uid, mapOf("avatarUrl" to imageUrl))
            if (success) {
                val currentState = _profileState.value
                if (currentState is ProfileState.Success) {
                    _profileState.value = ProfileState.Success(
                        currentState.user.copy(avatarUrl = imageUrl)
                    )
                }
            }
            _isUploading.value = false
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
            val success = userRepository.updateProfile(uid, updates)
            if (success) {
                val currentState = _profileState.value
                if (currentState is ProfileState.Success) {
                    _profileState.value = ProfileState.Success(
                        currentState.user.copy(
                            fullName = editFullName.text,
                            phoneNumber = editPhoneNumber.text
                        )
                    )
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
