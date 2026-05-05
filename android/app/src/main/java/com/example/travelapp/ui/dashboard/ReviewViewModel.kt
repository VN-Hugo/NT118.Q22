package com.example.travelapp.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Review
import com.example.travelapp.data.repository.ReviewRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReviewUiState {
    object Idle : ReviewUiState()
    object Loading : ReviewUiState()
    object Success : ReviewUiState()
    data class Error(val message: String) : ReviewUiState()
}

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var rating by mutableIntStateOf(5)
    var comment by mutableStateOf("")

    fun submitReview(booking: Booking) {
        if (comment.isBlank()) {
            _uiState.value = ReviewUiState.Error("Vui lòng nhập nhận xét của bạn")
            return
        }

        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            try {
                val uid = userRepository.getCurrentUserId() ?: return@launch
                val user = userRepository.getUserProfile(uid)
                
                val review = Review(
                    userId = uid,
                    bookId = booking.bookId,
                    proId = booking.proId,
                    username = user?.fullName ?: "Khách hàng",
                    userAvatar = user?.avatarUrl ?: "",
                    rating = rating,
                    comment = comment,
                    createdAt = System.currentTimeMillis()
                )

                val success = reviewRepository.submitReview(booking.proId, review)
                if (success) {
                    _uiState.value = ReviewUiState.Success
                } else {
                    _uiState.value = ReviewUiState.Error("Không thể gửi đánh giá. Vui lòng thử lại.")
                }
            } catch (e: Exception) {
                _uiState.value = ReviewUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetState() {
        _uiState.value = ReviewUiState.Idle
        rating = 5
        comment = ""
    }
}
