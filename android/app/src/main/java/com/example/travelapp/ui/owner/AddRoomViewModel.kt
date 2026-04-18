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

    // Chuyển sang TextFieldValue để hỗ trợ tiếng Việt tốt hơn
    var typeName by mutableStateOf(TextFieldValue(""))
    var price by mutableStateOf(TextFieldValue(""))
    var totalRooms by mutableStateOf(TextFieldValue(""))
    var amenitiesText by mutableStateOf(TextFieldValue(""))
    
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())

    private val _state = MutableStateFlow<AddRoomState>(AddRoomState.Idle)
    val state = _state.asStateFlow()

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
                // Upload ảnh
                val imageUrls = mutableListOf<String>()
                selectedImages.forEach { uri ->
                    val path = "properties/$proId/rooms/${UUID.randomUUID()}"
                    propertyRepository.uploadPropertyImage(path, uri)?.let { imageUrls.add(it) }
                }

                // Tạo đối tượng RoomType
                val roomType = RoomType(
                    typeName = typeName.text,
                    price = price.text.toDoubleOrNull() ?: 0.0,
                    totalRooms = totalRooms.text.toIntOrNull() ?: 1,
                    amenities = amenitiesText.text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                )

                // Lưu vào Firestore
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
