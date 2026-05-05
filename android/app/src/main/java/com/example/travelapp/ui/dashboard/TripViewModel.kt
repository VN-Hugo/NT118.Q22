package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.repository.BookingRepository
import com.example.travelapp.data.repository.ReviewRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripState {
    object Loading : TripState()
    data class Success(
        val upcoming: List<Booking>,
        val past: List<Booking>,
        val saved: List<Booking>,
        val reviewedBookingIds: Set<String> = emptySet()
    ) : TripState()
    data class Error(val message: String) : TripState()
}

@HiltViewModel
class TripViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _tripState = MutableStateFlow<TripState>(TripState.Loading)
    val tripState = _tripState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val uid = userRepository.getCurrentUserId()
        if (uid == null) {
            _tripState.value = TripState.Error("Người dùng chưa đăng nhập")
            return
        }

        viewModelScope.launch {
            _tripState.value = TripState.Loading
            
            // Kết hợp Bookings và Reviews để biết đơn nào đã đánh giá
            combine(
                bookingRepository.getUserBookings(uid),
                reviewRepository.getReviewsByUser(uid)
            ) { bookings, reviews ->
                val now = System.currentTimeMillis()
                val reviewedIds = reviews.map { it.bookId }.toSet()

                val upcoming = bookings.filter { 
                    it.endDate > now && (it.status == "confirmed" || it.status == "pending") 
                }
                
                // Past: Những chuyến đi đã xác nhận/hoàn thành và đã qua ngày trả phòng
                val past = bookings.filter { 
                    (it.endDate <= now && it.status == "confirmed") || it.status == "completed"
                }
                
                val saved = bookings.filter { 
                    it.status == "cancelled" || it.status == "rejected" 
                }

                TripState.Success(
                    upcoming = upcoming,
                    past = past,
                    saved = saved,
                    reviewedBookingIds = reviewedIds
                )
            }.catch { e ->
                _tripState.value = TripState.Error(e.message ?: "Lỗi tải dữ liệu. Hãy kiểm tra Firestore Index.")
            }.collect { state ->
                _tripState.value = state
            }
        }
    }

    fun cancelBooking(bookId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookId, "cancelled")
        }
    }
}
