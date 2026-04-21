package com.example.travelapp.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Booking
import com.example.travelapp.domain.model.HotelBookingDetails
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import com.example.travelapp.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.concurrent.TimeUnit

sealed class PropertyDetailState {
    object Loading : PropertyDetailState()
    data class Success(val property: Property, val roomTypes: List<RoomType>) : PropertyDetailState()
    data class Error(val message: String) : PropertyDetailState()
}

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    object Success : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val proId: String? = savedStateHandle["proId"]
    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<PropertyDetailState>(PropertyDetailState.Loading)
    val state = _state.asStateFlow()

    private val _bookingUiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingUiState = _bookingUiState.asStateFlow()

    // --- Trạng thái Booking ---
    var selectedRoomType by mutableStateOf<RoomType?>(null)
    var startDate by mutableStateOf<Long?>(null)
    var endDate by mutableStateOf<Long?>(null)
    var roomQuantity by mutableStateOf(1)

    val numberOfNights: Int
        get() {
            if (startDate == null || endDate == null) return 1
            val diff = endDate!! - startDate!!
            return TimeUnit.MILLISECONDS.toDays(diff).toInt().coerceAtLeast(1)
        }

    val totalBookingPrice: Double
        get() = (selectedRoomType?.price ?: 0.0) * numberOfNights * roomQuantity

    init {
        fetchPropertyAndRooms()
    }

    private fun fetchPropertyAndRooms() {
        if (proId == null) {
            _state.value = PropertyDetailState.Error("Mã khách sạn không tồn tại")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = PropertyDetailState.Loading
                val property = propertyRepository.getPropertyById(proId)
                val roomSnap = db.collection("Properties").document(proId)
                    .collection("RoomTypes").get().await()
                val roomTypes = roomSnap.toObjects(RoomType::class.java)

                if (property != null) {
                    _state.value = PropertyDetailState.Success(property, roomTypes)
                    // Mặc định chọn phòng đầu tiên
                    if (roomTypes.isNotEmpty()) selectedRoomType = roomTypes[0]
                } else {
                    _state.value = PropertyDetailState.Error("Không tìm thấy khách sạn")
                }
            } catch (e: Exception) {
                _state.value = PropertyDetailState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun onRoomTypeSelected(roomType: RoomType) {
        selectedRoomType = roomType
    }

    fun onDatesSelected(start: Long?, end: Long?) {
        startDate = start
        endDate = end
    }

    fun createBooking() {
        val uid = userRepository.getCurrentUserId()
        if (uid == null) {
            _bookingUiState.value = BookingUiState.Error("Vui lòng đăng nhập để đặt phòng")
            return
        }

        if (startDate == null || endDate == null || selectedRoomType == null) {
            _bookingUiState.value = BookingUiState.Error("Vui lòng chọn ngày và loại phòng")
            return
        }

        viewModelScope.launch {
            _bookingUiState.value = BookingUiState.Loading
            try {
                // 1. Check availability
                val available = propertyRepository.checkRoomAvailability(
                    proId!!, selectedRoomType!!.roomTypeId, startDate!!, endDate!!
                )

                if (available < roomQuantity) {
                    _bookingUiState.value = BookingUiState.Error("Rất tiếc, hạng phòng này đã hết chỗ")
                    return@launch
                }

                // 2. Tạo Booking
                val property = (state.value as? PropertyDetailState.Success)?.property
                val booking = Booking(
                    userId = uid,
                    proId = proId,
                    proName = property?.name ?: "",
                    proImage = property?.images?.firstOrNull()?.url ?: "",
                    startDate = startDate!!,
                    endDate = endDate!!,
                    totalPrice = totalBookingPrice,
                    status = "confirmed",
                    bookingType = "hotel",
                    hotelBooking = HotelBookingDetails(
                        roomTypeId = selectedRoomType!!.roomTypeId,
                        quantity = roomQuantity
                    )
                )

                val success = propertyRepository.createBooking(booking)
                if (success) {
                    _bookingUiState.value = BookingUiState.Success
                } else {
                    _bookingUiState.value = BookingUiState.Error("Lỗi hệ thống, vui lòng thử lại sau")
                }
            } catch (e: Exception) {
                _bookingUiState.value = BookingUiState.Error(e.message ?: "Lỗi kết nối")
            }
        }
    }

    fun resetBookingState() {
        _bookingUiState.value = BookingUiState.Idle
    }
}
