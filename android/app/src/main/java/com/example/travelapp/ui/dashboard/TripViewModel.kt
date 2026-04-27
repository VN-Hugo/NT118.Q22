package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.repository.BookingRepository
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
        val saved: List<Booking>
    ) : TripState()
    data class Error(val message: String) : TripState()
}

@HiltViewModel
class TripViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _tripState = MutableStateFlow<TripState>(TripState.Loading)
    val tripState = _tripState.asStateFlow()

    init {
        loadUserBookings()
    }

    private fun loadUserBookings() {
        val uid = userRepository.getCurrentUserId()
        if (uid == null) {
            _tripState.value = TripState.Error("Người dùng chưa đăng nhập")
            return
        }

        viewModelScope.launch {
            bookingRepository.getUserBookings(uid)
                .catch { e ->
                    _tripState.value = TripState.Error(e.message ?: "Lỗi tải dữ liệu")
                }
                .collect { allBookings ->
                    val now = System.currentTimeMillis()
                    
                    // Upcoming: Đơn đã xác nhận hoặc chờ duyệt và chưa kết thúc
                    val upcoming = allBookings.filter { 
                        it.endDate > now && (it.status == "confirmed" || it.status == "pending") 
                    }
                    // Past: Đơn đã kết thúc
                    val past = allBookings.filter { 
                        it.endDate <= now && it.status == "confirmed" 
                    }
                    // Saved: Các đơn đã bị hủy hoặc từ chối
                    val cancelled = allBookings.filter { 
                        it.status == "cancelled" || it.status == "rejected" 
                    }

                    _tripState.value = TripState.Success(
                        upcoming = upcoming,
                        past = past,
                        saved = cancelled
                    )
                }
        }
    }

    fun cancelBooking(bookId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookId, "cancelled")
        }
    }
}
