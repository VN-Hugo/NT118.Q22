package com.example.travelapp.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import com.example.travelapp.data.repository.BookingRepository
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingManagementState {
    object Loading : BookingManagementState()
    data class Success(
        val allBookings: List<Booking>,
        val hotels: List<Property>,
        val roomTypesMap: Map<String, List<RoomType>>
    ) : BookingManagementState()
    data class Error(val message: String) : BookingManagementState()
}

@HiltViewModel
class BookingManagementViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedHotelId = MutableStateFlow<String?>(null)
    val selectedHotelId = _selectedHotelId.asStateFlow()

    private val _selectedRoomTypeId = MutableStateFlow<String?>(null)
    val selectedRoomTypeId = _selectedRoomTypeId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookingState: StateFlow<BookingManagementState> = flow {
        val ownerId = userRepository.getCurrentUserId()
        if (ownerId == null) {
            emit(BookingManagementState.Error("Người dùng chưa đăng nhập"))
            return@flow
        }

        // 1. Lấy danh sách khách sạn của Owner (không lọc APPROVED)
        val hotelsFlow = propertyRepository.getProperties(status = null).map { list ->
            list.filter { it.ownerId == ownerId }
        }

        // 2. Lấy danh sách đơn đặt hàng
        val bookingsFlow = bookingRepository.getOwnerBookings(ownerId)

        // 3. Kết hợp và lấy thông tin phòng thời gian thực
        val combinedFlow = combine(hotelsFlow, bookingsFlow) { hotels, bookings ->
            hotels to bookings
        }.flatMapLatest { (hotels, bookings) ->
            if (hotels.isEmpty()) {
                flowOf(BookingManagementState.Success(bookings, emptyList(), emptyMap()))
            } else {
                val roomTypeFlows = hotels.map { hotel ->
                    propertyRepository.getRoomTypes(hotel.proId).map { hotel.proId to it }
                }
                combine(roomTypeFlows) { pairs ->
                    BookingManagementState.Success(bookings, hotels, pairs.toMap())
                }
            }
        }

        emitAll(combinedFlow)
    }.catch { e ->
        emit(BookingManagementState.Error(e.message ?: "Lỗi tải dữ liệu. Vui lòng kiểm tra Index Firebase."))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BookingManagementState.Loading)

    val filteredBookings: StateFlow<List<Booking>> = combine(
        bookingState, _selectedHotelId, _selectedRoomTypeId
    ) { state, hotelId, roomId ->
        if (state is BookingManagementState.Success) {
            state.allBookings.filter { booking ->
                val matchHotel = hotelId == null || booking.proId == hotelId
                val matchRoom = roomId == null || booking.hotelBooking?.roomTypeId == roomId
                matchHotel && matchRoom
            }
        } else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onHotelSelected(hotelId: String?) {
        _selectedHotelId.value = hotelId
        _selectedRoomTypeId.value = null
    }

    fun onRoomSelected(roomId: String?) {
        _selectedRoomTypeId.value = roomId
    }

    fun updateStatus(bookId: String, status: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookId, status)
        }
    }
}
