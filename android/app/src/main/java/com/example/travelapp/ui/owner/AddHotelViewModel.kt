package com.example.travelapp.ui.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.repository.PropertyRepository
import com.example.travelapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddHotelState {
    object Idle : AddHotelState()
    object Loading : AddHotelState()
    data class Success(val proId: String) : AddHotelState()
    data class Error(val message: String) : AddHotelState()
}

@HiltViewModel
class AddHotelViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var hotelName by mutableStateOf("")
    var address by mutableStateOf("")
    var desName by mutableStateOf("")
    var price by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedTags by mutableStateOf(setOf<String>())

    private val _state = MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val state = _state.asStateFlow()

    fun onTagToggle(tag: String) {
        selectedTags = if (selectedTags.contains(tag)) {
            selectedTags - tag
        } else {
            selectedTags + tag
        }
    }

    fun saveHotel() {
        val ownerId = userRepository.getCurrentUserId() ?: return
        if (hotelName.isEmpty() || address.isEmpty()) {
            _state.value = AddHotelState.Error("Vui lòng điền tên và địa chỉ")
            return
        }

        viewModelScope.launch {
            _state.value = AddHotelState.Loading
            val property = Property(
                ownerId = ownerId,
                name = hotelName,
                address = address,
                desName = desName,
                price = price.toDoubleOrNull() ?: 0.0,
                description = description,
                tags = selectedTags.toList(),
                type = "hotel"
            )
            
            val success = propertyRepository.saveProperty(property)
            if (success) {
                // Note: repository.saveProperty updates proId internally or returns true
                // For simplicity, we just assume it worked and navigate back or to room management
                _state.value = AddHotelState.Success(property.proId)
            } else {
                _state.value = AddHotelState.Error("Không thể lưu khách sạn")
            }
        }
    }
}
