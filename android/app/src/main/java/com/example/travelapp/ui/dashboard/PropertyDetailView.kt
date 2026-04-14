package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    onBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            if (state is PropertyDetailState.Success) {
                val property = (state as PropertyDetailState.Success).property
                BookingBottomBar(property)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is PropertyDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PropertyDetailState.Success -> {
                    PropertyDetailContent(currentState.property, currentState.roomTypes, onBack)
                }
                is PropertyDetailState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyDetailContent(property: Property, roomTypes: List<RoomType>, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // --- Image Header ---
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val primaryImage = property.images.firstOrNull { it.isPrimary }?.url ?: property.images.firstOrNull()?.url
            AsyncImage(
                model = primaryImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }

            // Favorite Button
            IconButton(
                onClick = { /* Toggle Favorite */ },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // --- Title & Rating ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(property.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(property.address, color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Text(property.averageRating.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Description ---
            Text("Mô tả", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                property.description.ifEmpty { "Chưa có mô tả chi tiết cho địa điểm này." },
                color = Color.Gray,
                lineHeight = 22.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Room Types Section ---
            if (roomTypes.isNotEmpty()) {
                Text("Chọn hạng phòng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                roomTypes.forEach { room ->
                    RoomTypeItem(room)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Amenities ---
            if (property.tags.isNotEmpty()) {
                Text("Tiện ích khách sạn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.padding(vertical = 12.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    property.tags.forEach { tag ->
                        AmenityChip(tag)
                    }
                }
            }
        }
    }
}

@Composable
fun RoomTypeItem(room: RoomType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(room.typeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = room.amenities.joinToString(" • "),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "đ${String.format("%,.0f", room.price)} / đêm",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            RadioButton(selected = false, onClick = { /* Select Room */ })
        }
    }
}

@Composable
fun AmenityChip(text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp)
    }
}

@Composable
fun BookingBottomBar(property: Property) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tổng thanh toán", color = Color.Gray, fontSize = 12.sp)
                Text("đ${String.format("%,.0f", property.price)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
            Button(
                onClick = { /* Proceed to payment */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Xác nhận đặt", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
