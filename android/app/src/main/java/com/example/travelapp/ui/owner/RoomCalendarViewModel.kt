package com.example.travelapp.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import com.example.travelapp.data.repository.BookingRepository
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(
        val hotels: List<Property>,
        val roomTypes: List<RoomType>,
        val occupancyMap: Map<Long, Int> // Timestamp -> Occupied Count
    ) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
}

@HiltViewModel
class RoomCalendarViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedHotelId = MutableStateFlow<String?>(null)
    val selectedHotelId = _selectedHotelId.asStateFlow()

    private val _selectedRoomTypeId = MutableStateFlow<String?>(null)
    val selectedRoomTypeId = _selectedRoomTypeId.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth = _currentMonth.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val ownerId = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            try {
                propertyRepository.getProperties().collect { allProperties ->
                    val ownerHotels = allProperties.filter { it.ownerId == ownerId }
                    if (ownerHotels.isNotEmpty() && _selectedHotelId.value == null) {
                        onHotelSelected(ownerHotels[0].proId)
                    }
                    updateState(hotels = ownerHotels)
                }
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun onHotelSelected(hotelId: String) {
        _selectedHotelId.value = hotelId
        _selectedRoomTypeId.value = null
        viewModelScope.launch {
            propertyRepository.getRoomTypes(hotelId).collect { rooms ->
                if (rooms.isNotEmpty()) {
                    onRoomTypeSelected(rooms[0].roomTypeId)
                }
                updateState(roomTypes = rooms)
            }
        }
    }

    fun onRoomTypeSelected(roomTypeId: String) {
        _selectedRoomTypeId.value = roomTypeId
        observeOccupancy()
    }

    fun changeMonth(delta: Int) {
        val newCal = _currentMonth.value.clone() as Calendar
        newCal.add(Calendar.MONTH, delta)
        _currentMonth.value = newCal
        observeOccupancy()
    }

    private fun observeOccupancy() {
        val hotelId = _selectedHotelId.value ?: return
        val roomId = _selectedRoomTypeId.value ?: return
        
        val cal = _currentMonth.value.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = cal.timeInMillis

        viewModelScope.launch {
            bookingRepository.getDailyOccupancy(hotelId, roomId, start, end).collect { map ->
                updateState(occupancy = map)
            }
        }
    }

    private fun updateState(
        hotels: List<Property>? = null,
        roomTypes: List<RoomType>? = null,
        occupancy: Map<Long, Int>? = null
    ) {
        val current = _uiState.value
        if (current is CalendarUiState.Success) {
            _uiState.value = current.copy(
                hotels = hotels ?: current.hotels,
                roomTypes = roomTypes ?: current.roomTypes,
                occupancyMap = occupancy ?: current.occupancyMap
            )
        } else {
            _uiState.value = CalendarUiState.Success(
                hotels = hotels ?: emptyList(),
                roomTypes = roomTypes ?: emptyList(),
                occupancyMap = occupancy ?: emptyMap()
            )
        }
    }
}
