package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExploreState {
    object Loading : ExploreState()
    data class Success(val properties: List<Property>, val favoriteIds: List<String>) : ExploreState()
    data class Error(val message: String) : ExploreState()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val user = userRepository.getUserProfile(uid)
            _favoriteIds.value = user?.favoriteIds ?: emptyList()
        }
    }

    // 1. Luồng lấy danh sách khách sạn (Lọc APPROVED ở đây)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val propertiesFlow = combine(
        _searchQuery.debounce(500L), 
        _selectedType
    ) { query, type ->
        if (query.isEmpty()) {
            propertyRepository.getProperties(type)
        } else {
            propertyRepository.searchProperties(query)
        }
    }.flatMapLatest { it }

    // 2. Luồng chính kết hợp Properties và Favorites (Tối ưu: Không fetch lại Firebase khi toggle tim)
    val exploreState: StateFlow<ExploreState> = combine(
        propertiesFlow,
        _favoriteIds
    ) { properties, favorites ->
        val approved = properties.filter { it.status == "APPROVED" }
        ExploreState.Success(approved, favorites)
    }.onStart<ExploreState> { // Chỉ định kiểu dữ liệu ở đây
        emit(ExploreState.Loading)
    }.catch { e ->
        emit(ExploreState.Error(e.message ?: "Lỗi tải dữ liệu"))
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

    fun toggleFavorite(propertyId: String) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            // Cập nhật lên Firebase
            val success = userRepository.toggleFavorite(uid, propertyId)
            if (success) {
                // Tải lại danh sách ID yêu thích để đồng bộ UI
                val user = userRepository.getUserProfile(uid)
                _favoriteIds.value = user?.favoriteIds ?: emptyList()
            }
        }
    }
}
