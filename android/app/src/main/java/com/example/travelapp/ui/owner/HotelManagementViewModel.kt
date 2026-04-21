package com.example.travelapp.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.repository.PropertyRepository
import com.example.travelapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HotelManagementState {
    object Loading : HotelManagementState()
    data class Success(val hotels: List<Property>) : HotelManagementState()
    data class Error(val message: String) : HotelManagementState()
}

@HiltViewModel
class HotelManagementViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val hotelState: StateFlow<HotelManagementState> = flow {
        emit(userRepository.getCurrentUserId())
    }.flatMapLatest { ownerId ->
        if (ownerId == null) {
            flowOf(HotelManagementState.Error("Người dùng chưa đăng nhập"))
        } else {
            propertyRepository.getProperties().map { allProperties ->
                val ownerHotels = allProperties.filter { it.ownerId == ownerId }
                HotelManagementState.Success(ownerHotels)
            }
        }
    }.onStart {
        emit(HotelManagementState.Loading)
    }.catch { e ->
        emit(HotelManagementState.Error(e.message ?: "Lỗi không xác định"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HotelManagementState.Loading
    )

    fun deleteHotel(proId: String) {
        viewModelScope.launch {
            propertyRepository.deleteProperty(proId)
        }
    }
}
