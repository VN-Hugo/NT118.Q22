package com.example.travelapp.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.HotelBookingDetails
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import com.example.travelapp.data.repository.BookingRepository
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val proId: String? = savedStateHandle["proId"]

    private val _state = MutableStateFlow<PropertyDetailState>(PropertyDetailState.Loading)
    val state = _state.asStateFlow()

    private val _bookingUiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingUiState = _bookingUiState.asStateFlow()

    // --- Booking form states ---
    var selectedRoomType by mutableStateOf<RoomType?>(null)
    var startDate by mutableStateOf<Long?>(null)
    var endDate by mutableStateOf<Long?>(null)
    var roomQuantity by mutableStateOf(1)

    val numberOfNights: Int
        get() {
            val start = startDate ?: return 1
            val end = endDate ?: return 1
            val diff = end - start
            return TimeUnit.MILLISECONDS.toDays(diff).toInt().coerceAtLeast(1)
        }

    val totalBookingPrice: Double
        get() = (selectedRoomType?.price ?: 0.0) * numberOfNights * roomQuantity

    init {
        loadData()
    }

    private fun loadData() {
        if (proId == null) {
            _state.value = PropertyDetailState.Error("Mã khách sạn không hợp lệ")
            return
        }

        viewModelScope.launch {
            _state.value = PropertyDetailState.Loading
            try {
                val property = propertyRepository.getPropertyById(proId)
                if (property == null) {
                    _state.value = PropertyDetailState.Error("Không tìm thấy thông tin khách sạn")
                    return@launch
                }

                propertyRepository.getRoomTypes(proId).collect { rooms ->
                    _state.value = PropertyDetailState.Success(property, rooms)
                    if (selectedRoomType == null && rooms.isNotEmpty()) {
                        selectedRoomType = rooms[0]
                    }
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
        val room = selectedRoomType
        val start = startDate
        val end = endDate
        val property = (state.value as? PropertyDetailState.Success)?.property

        if (uid == null) {
            _bookingUiState.value = BookingUiState.Error("Vui lòng đăng nhập để đặt phòng")
            return
        }
        if (room == null || start == null || end == null || property == null) {
            _bookingUiState.value = BookingUiState.Error("Vui lòng chọn đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _bookingUiState.value = BookingUiState.Loading
            try {
                // Check availability via BookingRepository
                val available = bookingRepository.checkRoomAvailability(
                    proId!!, room.roomTypeId, start, end
                )

                if (available < roomQuantity) {
                    _bookingUiState.value = BookingUiState.Error("Rất tiếc, hạng phòng này đã hết chỗ trong thời gian này")
                    return@launch
                }

                val booking = Booking(
                    userId = uid,
                    ownerId = property.ownerId, // QUAN TRỌNG: Phải gán ownerId để Owner có thể thấy đơn này
                    proId = proId,
                    proName = property.name,
                    proImage = property.images.firstOrNull()?.url ?: "",
                    startDate = start,
                    endDate = end,
                    totalPrice = totalBookingPrice,
                    status = "pending", // Mặc định là chờ duyệt
                    bookingType = "hotel",
                    hotelBooking = HotelBookingDetails(
                        roomTypeId = room.roomTypeId,
                        quantity = roomQuantity
                    )
                )

                val success = bookingRepository.createBooking(booking)
                if (success) {
                    _bookingUiState.value = BookingUiState.Success
                } else {
                    _bookingUiState.value = BookingUiState.Error("Đặt phòng thất bại, vui lòng thử lại")
                }
            } catch (e: Exception) {
                _bookingUiState.value = BookingUiState.Error("Lỗi kết nối hệ thống")
            }
        }
    }

    fun resetBookingState() {
        _bookingUiState.value = BookingUiState.Idle
    }
}
