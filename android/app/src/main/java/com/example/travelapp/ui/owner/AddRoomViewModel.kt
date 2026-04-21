package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class AddRoomState {
    object Idle : AddRoomState()
    object Loading : AddRoomState()
    object Success : AddRoomState()
    data class Error(val message: String) : AddRoomState()
}

@HiltViewModel
class AddRoomViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val proId: String = savedStateHandle["proId"] ?: ""
    val roomTypeId: String? = savedStateHandle["roomTypeId"] // Null if adding new

    var typeName by mutableStateOf(TextFieldValue(""))
    var price by mutableStateOf(TextFieldValue(""))
    var totalRooms by mutableStateOf(TextFieldValue(""))
    
    // Change to selectable list like Hotel tags
    var selectedAmenities by mutableStateOf(setOf<String>())
    
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())

    private val _state = MutableStateFlow<AddRoomState>(AddRoomState.Idle)
    val state = _state.asStateFlow()

    val availableAmenities = listOf(
        "Wifi", "Điều hòa", "Bồn tắm", "Tivi", "Tủ lạnh", 
        "Ban công", "Máy sấy tóc", "Két sắt", "Bàn làm việc"
    )

    init {
        if (roomTypeId != null) {
            loadRoomData()
        }
    }

    private fun loadRoomData() {
        viewModelScope.launch {
            _state.value = AddRoomState.Loading
            try {
                val rooms = propertyRepository.getRoomTypes(proId).first()
                val room = rooms.find { it.roomTypeId == roomTypeId }
                if (room != null) {
                    typeName = TextFieldValue(room.typeName)
                    price = TextFieldValue(room.price.toLong().toString())
                    totalRooms = TextFieldValue(room.totalRooms.toString())
                    selectedAmenities = room.amenities.toSet()
                    _state.value = AddRoomState.Idle
                }
            } catch (e: Exception) {
                _state.value = AddRoomState.Error("Lỗi tải thông tin phòng")
            }
        }
    }

    fun onAmenityToggle(amenity: String) {
        selectedAmenities = if (selectedAmenities.contains(amenity)) {
            selectedAmenities - amenity
        } else {
            selectedAmenities + amenity
        }
    }

    fun onImagesSelected(uris: List<Uri>) {
        selectedImages = uris
    }

    fun saveRoom() {
        if (proId.isEmpty()) return
        if (typeName.text.isEmpty() || price.text.isEmpty()) {
            _state.value = AddRoomState.Error("Vui lòng điền tên và giá phòng")
            return
        }

        viewModelScope.launch {
            _state.value = AddRoomState.Loading
            
            try {
                // Upload logic (placeholder for actual Cloudinary/Firebase integration)
                val imageUrls = mutableListOf<String>()
                // ... logic upload ...

                val roomType = RoomType(
                    roomTypeId = roomTypeId ?: "", // Keep ID if editing
                    typeName = typeName.text,
                    price = price.text.toDoubleOrNull() ?: 0.0,
                    totalRooms = totalRooms.text.toIntOrNull() ?: 1,
                    amenities = selectedAmenities.toList()
                )

                val success = propertyRepository.saveRoomType(proId, roomType)
                if (success) {
                    _state.value = AddRoomState.Success
                } else {
                    _state.value = AddRoomState.Error("Không thể lưu thông tin phòng")
                }
            } catch (e: Exception) {
                _state.value = AddRoomState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}
