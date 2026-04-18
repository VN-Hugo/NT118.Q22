package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExploreState {
    object Loading : ExploreState()
    data class Success(val properties: List<Property>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null) // null for all, "hotel", "activity"
    val selectedType = _selectedType.asStateFlow()

    val exploreState: StateFlow<ExploreState> = combine(_searchQuery, _selectedType) { query, type ->
        Pair(query, type)
    }.flatMapLatest { (query, type) ->
        if (query.isEmpty()) {
            propertyRepository.getProperties(type)
        } else {
            propertyRepository.searchProperties(query)
        }
    }.map { properties ->
        // Chỉ hiển thị những khách sạn đã được phê duyệt (APPROVED)
        val approvedProperties = properties.filter { it.status == "APPROVED" }
        ExploreState.Success(approvedProperties) as ExploreState
    }.onStart {
        emit(ExploreState.Loading)
    }.catch { e ->
        emit(ExploreState.Error(e.message ?: "Unknown Error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeChange(type: String?) {
        _selectedType.value = type
    }
}