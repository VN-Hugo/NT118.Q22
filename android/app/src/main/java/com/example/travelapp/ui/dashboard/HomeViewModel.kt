package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.usecase.GetPropertiesUseCase
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
    private val getPropertiesUseCase: GetPropertiesUseCase
) : ViewModel() {

    val homeState: StateFlow<HomeState> = getPropertiesUseCase("hotel")
        .map { hotels ->
            HomeState.Success(hotels) as HomeState
        }
        .onStart { emit(HomeState.Loading) }
        .catch { e -> emit(HomeState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )
}