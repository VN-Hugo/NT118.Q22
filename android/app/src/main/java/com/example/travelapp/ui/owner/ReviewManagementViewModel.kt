package com.example.travelapp.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.Review
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.ReviewRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class ReviewManagementState {
    object Loading : ReviewManagementState()
    data class Success(
        val hotels: List<Property>,
        val reviews: List<Review>,
        val selectedHotel: Property?
    ) : ReviewManagementState()
    data class Error(val message: String) : ReviewManagementState()
}

@HiltViewModel
class ReviewManagementViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedHotelId = MutableStateFlow<String?>(null)
    val selectedHotelId = _selectedHotelId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ReviewManagementState> = flow {
        val ownerId = userRepository.getCurrentUserId()
        if (ownerId == null) {
            emit(ReviewManagementState.Error("Chưa đăng nhập"))
            return@flow
        }

        // 1. Lấy danh sách khách sạn của Owner
        val hotelsFlow = propertyRepository.getProperties(status = null).map { list ->
            list.filter { it.ownerId == ownerId }
        }

        emitAll(
            combine(hotelsFlow, _selectedHotelId) { hotels, selectedId ->
                hotels to selectedId
            }.flatMapLatest { (hotels, selectedId) ->
                val targetHotel = if (selectedId == null && hotels.isNotEmpty()) hotels[0] 
                                 else hotels.find { it.proId == selectedId }
                
                if (targetHotel == null) {
                    flowOf(ReviewManagementState.Success(hotels, emptyList(), null))
                } else {
                    reviewRepository.getReviewsByProperty(targetHotel.proId).map { reviews ->
                        ReviewManagementState.Success(hotels, reviews, targetHotel)
                    }
                }
            }
        )
    }.catch { e ->
        emit(ReviewManagementState.Error(e.message ?: "Lỗi tải dữ liệu"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewManagementState.Loading)

    fun selectHotel(hotelId: String) {
        _selectedHotelId.value = hotelId
    }
}
