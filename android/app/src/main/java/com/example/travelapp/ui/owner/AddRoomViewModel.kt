package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var typeName by mutableStateOf("")
    var price by mutableStateOf("")
    var totalRooms by mutableStateOf("")
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())
    var amenitiesText by mutableStateOf("")

    private val _state = MutableStateFlow<AddRoomState>(AddRoomState.Idle)
    val state = _state.asStateFlow()

    fun onImagesSelected(uris: List<Uri>) {
        selectedImages = uris
    }

    fun saveRoom() {
        if (proId.isEmpty()) return
        if (typeName.isEmpty() || price.isEmpty()) {
            _state.value = AddRoomState.Error("Vui lòng điền tên và giá phòng")
            return
        }

        viewModelScope.launch {
            _state.value = AddRoomState.Loading
            
            // 1. Upload ảnh (nếu có) - Ở đây tôi làm đơn giản là upload lên Storage
            // Thực tế model RoomType hiện tại chưa có list images, bạn có thể thêm sau
            // Tôi sẽ demo logic upload để bạn thấy cách hoạt động
            val imageUrls = mutableListOf<String>()
            selectedImages.forEach { uri ->
                val path = "properties/$proId/rooms/${UUID.randomUUID()}"
                propertyRepository.uploadPropertyImage(path, uri)?.let { imageUrls.add(it) }
            }

            // 2. Tạo đối tượng RoomType
            val roomType = RoomType(
                typeName = typeName,
                price = price.toDoubleOrNull() ?: 0.0,
                totalRooms = totalRooms.toIntOrNull() ?: 1,
                amenities = amenitiesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            )

            // 3. Lưu vào Firestore
            val success = propertyRepository.saveRoomType(proId, roomType)
            if (success) {
                _state.value = AddRoomState.Success
            } else {
                _state.value = AddRoomState.Error("Không thể lưu thông tin phòng")
            }
        }
    }
}
