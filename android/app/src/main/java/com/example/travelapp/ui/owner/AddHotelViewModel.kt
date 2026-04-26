package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.PropertyImage
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val proId: String? = savedStateHandle["proId"]

    var hotelName by mutableStateOf(TextFieldValue(""))
    var address by mutableStateOf(TextFieldValue(""))
    var latitude by mutableStateOf(10.762622)
    var longitude by mutableStateOf(106.660172)
    var desName by mutableStateOf("")
    var description by mutableStateOf(TextFieldValue(""))
    var selectedTags by mutableStateOf(setOf<String>())
    var selectedImages by mutableStateOf<List<Uri>>(emptyList())
    
    private var originalProperty: Property? = null

    private val _state = MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val state = _state.asStateFlow()

    val provinceList = listOf(
        "Thành phố Hà Nội", "Thành phố Hồ Chí Minh", "Thành phố Hải Phòng", "Thành phố Đà Nẵng",
        "Thành phố Cần Thơ", "Thành phố Huế", "Tỉnh An Giang", "Tỉnh Bắc Ninh", "Tỉnh Cao Bằng",
        "Tỉnh Cà Mau", "Tỉnh Điện Biên", "Tỉnh Đắk Lắk", "Tỉnh Đồng Nai", "Tỉnh Đồng Tháp",
        "Tỉnh Gia Lai", "Tỉnh Hà Tĩnh", "Tỉnh Hưng Yên", "Tỉnh Khánh Hòa", "Tỉnh Lai Châu",
        "Tỉnh Lào Cai", "Tỉnh Lâm Đồng", "Tỉnh Lạng Sơn", "Tỉnh Nghệ An", "Tỉnh Ninh Bình",
        "Tỉnh Phú Thọ", "Tỉnh Quảng Ngãi", "Tỉnh Quảng Ninh", "Tỉnh Quảng Trị", "Tỉnh Sơn La",
        "Tỉnh Thanh Hóa", "Tỉnh Thái Nguyên", "Tỉnh Tuyên Quang", "Tỉnh Tây Ninh", "Tỉnh Vĩnh Long"
    )

    init {
        if (proId != null) {
            loadHotelData(proId)
        }
    }

    private fun loadHotelData(id: String) {
        viewModelScope.launch {
            _state.value = AddHotelState.Loading
            val property = propertyRepository.getPropertyById(id)
            if (property != null) {
                originalProperty = property
                hotelName = TextFieldValue(property.name)
                address = TextFieldValue(property.address)
                latitude = property.latitude
                longitude = property.longitude
                desName = property.desName
                description = TextFieldValue(property.description)
                selectedTags = property.tags.toSet()
                _state.value = AddHotelState.Idle
            } else {
                _state.value = AddHotelState.Error("Không tìm thấy thông tin khách sạn")
            }
        }
    }

    fun onTagToggle(tag: String) {
        selectedTags = if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag
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
                    continuation.resume(resultData?.get("secure_url") as? String)
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
                val uploadedImages = mutableListOf<PropertyImage>()
                originalProperty?.images?.let { uploadedImages.addAll(it) }

                selectedImages.forEachIndexed { index, uri ->
                    uploadImageToCloudinary(uri)?.let { url ->
                        val isPrimary = uploadedImages.isEmpty() && index == 0
                        uploadedImages.add(PropertyImage(url = url, isPrimary = isPrimary))
                    }
                }

                // Khi lưu hotel info, chuyển trạng thái sang PENDING và bỏ qua DRAFT
                val property = Property(
                    proId = proId ?: "",
                    ownerId = ownerId,
                    name = hotelName.text,
                    address = address.text,
                    latitude = latitude,
                    longitude = longitude,
                    desName = desName,
                    description = description.text,
                    tags = selectedTags.toList(),
                    images = uploadedImages,
                    type = "hotel",
                    status = "PENDING" 
                )
                
                val resultId = propertyRepository.saveProperty(property)
                if (resultId != null) {
                    _state.value = AddHotelState.Success(resultId)
                } else {
                    _state.value = AddHotelState.Error("Không thể lưu khách sạn")
                }
            } catch (e: Exception) {
                _state.value = AddHotelState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}
