package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Plan
import com.example.travelapp.data.repository.BookingRepository
import com.example.travelapp.data.repository.PlanRepository
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
        val plans: List<Plan> = emptyList()
    ) : TripState()
    data class Error(val message: String) : TripState()
}

@HiltViewModel
class TripViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository
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
            // Combine bookings and plans into a single state
            combine(
                bookingRepository.getUserBookings(uid),
                planRepository.getUserPlans(uid)
            ) { allBookings, allPlans ->
                val now = System.currentTimeMillis()
                
                // Upcoming: Đơn đã xác nhận hoặc chờ duyệt và chưa kết thúc
                val upcoming = allBookings.filter { 
                    it.endDate > now && (it.status == "confirmed" || it.status == "pending") 
                }.sortedByDescending { it.startDate }
                // Past: Đơn đã kết thúc
                val past = allBookings.filter { 
                    it.endDate <= now && it.status == "confirmed" 
                }.sortedByDescending { it.startDate }
                // Saved: Các đơn đã bị hủy hoặc từ chối
                val cancelled = allBookings.filter { 
                    it.status == "cancelled" || it.status == "rejected" 
                }.sortedByDescending { it.startDate }

                val sortedPlans = allPlans.sortedByDescending { it.createdAt }

                TripState.Success(
                    upcoming = upcoming,
                    past = past,
                    saved = cancelled,
                    plans = sortedPlans
                )
            }
            .catch { e ->
                _tripState.value = TripState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
            .collect { state ->
                _tripState.value = state
            }
        }
    }

    fun cancelBooking(bookId: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookId, "cancelled")
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            planRepository.deletePlan(planId)
        }
    }
}
