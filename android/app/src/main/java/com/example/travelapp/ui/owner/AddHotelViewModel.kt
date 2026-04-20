package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    var hotelName by mutableStateOf(TextFieldValue(""))
    var address by mutableStateOf(TextFieldValue(""))
    var desName by mutableStateOf("") // Changed to String for dropdown selection
    var description by mutableStateOf(TextFieldValue(""))
    
    var selectedTags by mutableStateOf(setOf<String>())
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())

    private val _state = MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val state = _state.asStateFlow()

    val provinceList = listOf(
        "Thành phố Hà Nội",
        "Thành phố Hồ Chí Minh",
        "Thành phố Hải Phòng",
        "Thành phố Đà Nẵng",
        "Thành phố Cần Thơ",
        "Thành phố Huế",
        "Tỉnh An Giang",
        "Tỉnh Bắc Ninh",
        "Tỉnh Cao Bằng",
        "Tỉnh Cà Mau",
        "Tỉnh Điện Biên",
        "Tỉnh Đắk Lắk",
        "Tỉnh Đồng Nai",
        "Tỉnh Đồng Tháp",
        "Tỉnh Gia Lai",
        "Tỉnh Hà Tĩnh",
        "Tỉnh Hưng Yên",
        "Tỉnh Khánh Hòa",
        "Tỉnh Lai Châu",
        "Tỉnh Lào Cai",
        "Tỉnh Lâm Đồng",
        "Tỉnh Lạng Sơn",
        "Tỉnh Nghệ An",
        "Tỉnh Ninh Bình",
        "Tỉnh Phú Thọ",
        "Tỉnh Quảng Ngãi",
        "Tỉnh Quảng Ninh",
        "Tỉnh Quảng Trị",
        "Tỉnh Sơn La",
        "Tỉnh Thanh Hóa",
        "Tỉnh Thái Nguyên",
        "Tỉnh Tuyên Quang",
        "Tỉnh Tây Ninh",
        "Tỉnh Vĩnh Long"
    )

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

    private suspend fun uploadImageToCloudinary(uri: Uri): String? = suspendCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .option("upload_preset", "travel_app_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String
                    continuation.resume(imageUrl)
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    continuation.resume(null)
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    continuation.resume(null)
                }
            }).dispatch()
    }

    fun saveHotel() {
        val ownerId = userRepository.getCurrentUserId() ?: return
        if (hotelName.text.isEmpty() || address.text.isEmpty() || desName.isEmpty()) {
            _state.value = AddHotelState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _state.value = AddHotelState.Loading
            
            try {
                // 1. Upload images to Cloudinary
                val uploadedImages = mutableListOf<PropertyImage>()
                selectedImages.forEachIndexed { index, uri ->
                    val downloadUrl = uploadImageToCloudinary(uri)
                    if (downloadUrl != null) {
                        uploadedImages.add(PropertyImage(url = downloadUrl, isPrimary = index == 0))
                    }
                }

                // 2. Save property details
                val property = Property(
                    ownerId = ownerId,
                    name = hotelName.text,
                    address = address.text,
                    desName = desName,
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
