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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingManagementState {
    object Loading : BookingManagementState()
    data class Success(
        val allBookings: List<Booking>,
        val hotels: List<Property>,
        val roomTypesMap: Map<String, List<RoomType>> // proId -> List<RoomType>
    ) : BookingManagementState()
    data class Error(val message: String) : BookingManagementState()
}

@HiltViewModel
class BookingManagementViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _bookingState = MutableStateFlow<BookingManagementState>(BookingManagementState.Loading)
    val bookingState = _bookingState.asStateFlow()

    // Filters
    private val _selectedHotelId = MutableStateFlow<String?>(null)
    val selectedHotelId = _selectedHotelId.asStateFlow()

    private val _selectedRoomTypeId = MutableStateFlow<String?>(null)
    val selectedRoomTypeId = _selectedRoomTypeId.asStateFlow()

    val filteredBookings: StateFlow<List<Booking>> = combine(
        _bookingState, _selectedHotelId, _selectedRoomTypeId
    ) { state, hotelId, roomId ->
        if (state is BookingManagementState.Success) {
            state.allBookings.filter { booking ->
                val matchHotel = hotelId == null || booking.proId == hotelId
                val matchRoom = roomId == null || booking.hotelBooking?.roomTypeId == roomId
                matchHotel && matchRoom
            }
        } else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val ownerId = userRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            try {
                // 1. Lấy danh sách khách sạn của Owner
                propertyRepository.getProperties().collect { allProperties ->
                    val ownerHotels = allProperties.filter { it.ownerId == ownerId }
                    
                    // 2. Lấy đơn đặt phòng
                    bookingRepository.getOwnerBookings(ownerId).collect { bookings ->
                        
                        // 3. Lấy RoomTypes cho từng khách sạn (để làm filter)
                        val roomMap = mutableMapOf<String, List<RoomType>>()
                        ownerHotels.forEach { hotel ->
                            // Lưu ý: collect ở đây có thể gây block, thực tế nên dùng combine
                            val rooms = propertyRepository.getRoomTypes(hotel.proId).first()
                            roomMap[hotel.proId] = rooms
                        }

                        _bookingState.value = BookingManagementState.Success(
                            allBookings = bookings.sortedByDescending { it.startDate },
                            hotels = ownerHotels,
                            roomTypesMap = roomMap
                        )
                    }
                }
            } catch (e: Exception) {
                _bookingState.value = BookingManagementState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun onHotelSelected(hotelId: String?) {
        _selectedHotelId.value = hotelId
        _selectedRoomTypeId.value = null // Reset room filter when hotel changes
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
