package com.example.travelapp.ui.owner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoomManagementState {
    object Loading : RoomManagementState()
    data class Success(val rooms: List<RoomType>) : RoomManagementState()
    data class Error(val message: String) : RoomManagementState()
}

@HiltViewModel
class RoomManagementViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val proId: String = savedStateHandle["proId"] ?: ""

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage = _uiMessage.asStateFlow()

    val roomState: StateFlow<RoomManagementState> = if (proId.isEmpty()) {
        MutableStateFlow(RoomManagementState.Error("Không tìm thấy mã khách sạn"))
    } else {
        propertyRepository.getRoomTypes(proId)
            .map { RoomManagementState.Success(it) as RoomManagementState }
            .onStart { emit(RoomManagementState.Loading) }
            .catch { emit(RoomManagementState.Error(it.message ?: "Lỗi tải danh sách phòng")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RoomManagementState.Loading
            )
    }

    fun deleteRoom(roomTypeId: String) {
        val currentState = roomState.value
        if (currentState is RoomManagementState.Success) {
            // Ràng buộc bảo vệ: Phải có ít nhất 1 hạng phòng
            if (currentState.rooms.size <= 1) {
                _uiMessage.value = "Khách sạn phải có ít nhất một hạng phòng hoạt động."
                return
            }
            viewModelScope.launch {
                propertyRepository.deleteRoomType(proId, roomTypeId)
            }
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}
