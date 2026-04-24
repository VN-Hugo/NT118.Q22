package com.example.travelapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WishlistState {
    object Loading : WishlistState()
    data class Success(val hotels: List<Property>) : WishlistState()
    data class Error(val message: String) : WishlistState()
}

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<WishlistState>(WishlistState.Loading)
    val wishlistState = _wishlistState.asStateFlow()

    init {
        loadWishlist()
    }

    fun loadWishlist() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _wishlistState.value = WishlistState.Loading
            
            // Lắng nghe thay đổi User từ Flow để cập nhật danh sách ID mới nhất
            userRepository.getUserFlow(uid).collect { user ->
                val favoriteIds = user?.favoriteIds ?: emptyList()
                
                if (favoriteIds.isEmpty()) {
                    _wishlistState.value = WishlistState.Success(emptyList())
                } else {
                    // Lấy thông tin chi tiết từng khách sạn từ Repository
                    propertyRepository.getProperties().collect { allProperties ->
                        val favoriteHotels = allProperties.filter { it.proId in favoriteIds }
                        _wishlistState.value = WishlistState.Success(favoriteHotels)
                    }
                }
            }
        }
    }

    fun removeFromWishlist(propertyId: String) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            userRepository.toggleFavorite(uid, propertyId)
            // Wishlist sẽ tự động cập nhật nhờ getUserFlow phía trên
        }
    }
}
