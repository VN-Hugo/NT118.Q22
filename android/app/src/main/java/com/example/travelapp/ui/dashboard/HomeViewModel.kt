package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val suggestedHotels: List<Property>) : HomeState()
    data class Error(val message: String) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    val homeState: StateFlow<HomeState> = propertyRepository.getProperties("hotel")
        .map { hotels ->
            HomeState.Success(hotels) as HomeState
        }
        .onStart { emit(HomeState.Loading) }
        .catch { e -> emit(HomeState.Error(e.message ?: "Lỗi tải dữ liệu")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )
}
