package com.example.travelapp.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class PropertyDetailState {
    object Loading : PropertyDetailState()
    data class Success(val property: Property, val roomTypes: List<RoomType>) : PropertyDetailState()
    data class Error(val message: String) : PropertyDetailState()
}

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val proId: String? = savedStateHandle["proId"]
    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<PropertyDetailState>(PropertyDetailState.Loading)
    val state = _state.asStateFlow()

    init {
        fetchPropertyAndRooms()
    }

    private fun fetchPropertyAndRooms() {
        if (proId == null) {
            _state.value = PropertyDetailState.Error("Property ID is missing")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = PropertyDetailState.Loading
                
                // 1. Lấy thông tin khách sạn
                val property = propertyRepository.getPropertyById(proId)
                
                // 2. Lấy danh sách phòng từ sub-collection
                val roomSnap = db.collection("Properties").document(proId)
                    .collection("RoomTypes").get().await()
                val roomTypes = roomSnap.toObjects(RoomType::class.java)

                if (property != null) {
                    _state.value = PropertyDetailState.Success(property, roomTypes)
                } else {
                    _state.value = PropertyDetailState.Error("Property not found")
                }
            } catch (e: Exception) {
                _state.value = PropertyDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
