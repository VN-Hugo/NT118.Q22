package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.PropertyImage
import com.example.travelapp.domain.repository.PropertyRepository
import com.example.travelapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
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

    // Chuyển sang TextFieldValue để hỗ trợ bộ gõ tiếng Việt tốt hơn
    var hotelName by mutableStateOf(TextFieldValue(""))
    var address by mutableStateOf(TextFieldValue(""))
    var desName by mutableStateOf(TextFieldValue(""))
    var description by mutableStateOf(TextFieldValue(""))
    
    var selectedTags by mutableStateOf(setOf<String>())
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())

    private val _state = MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val state = _state.asStateFlow()

    fun onTagToggle(tag: String) {
        selectedTags = if (selectedTags.contains(tag)) {
            selectedTags - tag
        } else {
            selectedTags + tag
        }
    }

    fun onImagesSelected(uris: List<Uri>) {
        selectedImages = uris
    }

    fun saveHotel() {
        val ownerId = userRepository.getCurrentUserId() ?: return
        if (hotelName.text.isEmpty() || address.text.isEmpty()) {
            _state.value = AddHotelState.Error("Vui lòng điền tên và địa chỉ")
            return
        }

        viewModelScope.launch {
            _state.value = AddHotelState.Loading
            
            try {
                val uploadedImages = mutableListOf<PropertyImage>()
                selectedImages.forEachIndexed { index, uri ->
                    val path = "properties/${UUID.randomUUID()}"
                    val downloadUrl = propertyRepository.uploadPropertyImage(path, uri)
                    if (downloadUrl != null) {
                        uploadedImages.add(PropertyImage(url = downloadUrl, isPrimary = index == 0))
                    }
                }

                val property = Property(
                    ownerId = ownerId,
                    name = hotelName.text,
                    address = address.text,
                    desName = desName.text,
                    description = description.text,
                    tags = selectedTags.toList(),
                    images = uploadedImages,
                    type = "hotel",
                    status = "PENDING"
                )
                
                val success = propertyRepository.saveProperty(property)
                if (success) {
                    _state.value = AddHotelState.Success(property.proId)
                } else {
                    _state.value = AddHotelState.Error("Không thể lưu khách sạn")
                }
            } catch (e: Exception) {
                _state.value = AddHotelState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}
