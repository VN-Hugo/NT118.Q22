package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelapp.data.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.wishlistState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách yêu thích", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            when (val currentState = state) {
                is WishlistState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WishlistState.Success -> {
                    if (currentState.hotels.isEmpty()) {
                        EmptyWishlist(onExploreClick = onBack)
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(currentState.hotels) { hotel ->
                                // Sử dụng lại PropertyCard từ ExploreView hoặc thiết kế card ngang riêng
                                WishlistCard(
                                    hotel = hotel,
                                    onClick = { onPropertyClick(hotel.proId) },
                                    onRemove = { viewModel.removeFromWishlist(hotel.proId) }
                                )
                            }
                        }
                    }
                }
                is WishlistState.Error -> {
                    Text(currentState.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun EmptyWishlist(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Chưa có địa điểm nào được lưu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Hãy thả tim những khách sạn bạn yêu thích nhé!", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onExploreClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Khám phá ngay")
        }
    }
}

@Composable
fun WishlistCard(hotel: Property, onClick: () -> Unit, onRemove: () -> Unit) {
    // Để tiết kiệm thời gian, tui dùng PropertyCard nhưng bạn có thể thiết kế lại kiểu Card ngang (List tile)
    PropertyCard(
        property = hotel,
        isFavorite = true,
        onClick = onClick,
        onToggleFavorite = onRemove
    )
}
