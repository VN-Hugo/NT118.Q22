package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.domain.model.Property
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelManagementScreen(
    onAddHotelClick: () -> Unit,
    onEditHotelClick: (String) -> Unit,
    onManageRoomsClick: (String) -> Unit,
    viewModel: HotelManagementViewModel = hiltViewModel()
) {
    val hotelState by viewModel.hotelState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddHotelClick,
                containerColor = Color(0xFF005D67),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm khách sạn mới", fontWeight = FontWeight.Bold)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text("HỆ THỐNG QUẢN LÝ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("Danh sách khách sạn", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005D67))
                Spacer(modifier = Modifier.height(16.dp))
                QuoteBanner()
            }

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Tất cả", "PENDING", "APPROVED", "REJECTED")
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { 
                            Text(when(filter) {
                                "PENDING" -> "Chờ duyệt"
                                "APPROVED" -> "Đã duyệt"
                                "REJECTED" -> "Từ chối"
                                else -> "Tất cả"
                            }) 
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF005D67),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // List Content
            when (val state = hotelState) {
                is HotelManagementState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF005D67))
                    }
                }
                is HotelManagementState.Success -> {
                    val filteredHotels = if (selectedFilter == "Tất cả") {
                        state.hotels
                    } else {
                        state.hotels.filter { it.status == selectedFilter }
                    }

                    if (filteredHotels.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (selectedFilter == "Tất cả") "Bạn chưa có khách sạn nào." else "Không có khách sạn nào ở trạng thái này.",
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(filteredHotels) { hotel ->
                                HotelManagementCard(
                                    hotel = hotel,
                                    onEditClick = { onEditHotelClick(hotel.proId) },
                                    onDeleteClick = { viewModel.deleteHotel(hotel.proId) },
                                    onManageRoomsClick = { onManageRoomsClick(hotel.proId) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
                is HotelManagementState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Lỗi: ${state.message}", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteBanner() {
    Surface(
        color = Color(0xFFE0F2F1),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFF005D67))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "\"Sự hài lòng của khách hàng là thước đo thành công lớn nhất của người làm dịch vụ.\"",
                color = Color(0xFF005D67),
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun HotelManagementCard(
    hotel: Property,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onManageRoomsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onManageRoomsClick() }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val imageUrl = hotel.images.firstOrNull { it.isPrimary }?.url ?: hotel.images.firstOrNull()?.url
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = hotel.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }

                // Badge Trạng thái (Góc trái dưới)
                val (statusText, statusColor) = when(hotel.status) {
                    "APPROVED" -> "ĐÃ DUYỆT" to Color(0xFF2E7D32)
                    "REJECTED" -> "BỊ TỪ CHỐI" to Color(0xFFC62828)
                    else -> "CHỜ DUYỆT" to Color(0xFFEF6C00)
                }

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 0.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                
                // Rating Badge (Góc phải trên)
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                        Text("${hotel.averageRating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF003339))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(hotel.address, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                    }
                }
            }
        }
    }
}
