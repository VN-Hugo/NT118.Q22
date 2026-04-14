package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onManageRoomsClick: (String) -> Unit,
    viewModel: HotelManagementViewModel = hiltViewModel()
) {
    val hotelState by viewModel.hotelState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddHotelClick,
                containerColor = Color(0xFF005D67), // BrandTeal
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm khách sạn mới", fontWeight = FontWeight.Bold)
            }
        },
    ) { innerPadding ->
        when (val state = hotelState) {
            is HotelManagementState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF005D67))
                }
            }
            is HotelManagementState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("DANH SÁCH SỞ HỮU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("Quản lý khách sạn", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005D67))

                        Spacer(modifier = Modifier.height(16.dp))
                        QuoteBanner()
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.hotels.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("Bạn chưa đăng ký khách sạn nào.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(state.hotels) { hotel ->
                            HotelManagementCard(hotel, onClick = { onManageRoomsClick(hotel.proId) })
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF005D67))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "\"Dịch vụ khách hàng tốt không chỉ là giải quyết vấn đề, mà là tạo ra những kỷ niệm đáng nhớ.\"",
                color = Color(0xFF005D67),
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun HotelManagementCard(hotel: Property, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shadowElevation = 2.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val imageUrl = hotel.images.firstOrNull { it.isPrimary }?.url ?: hotel.images.firstOrNull()?.url
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                        Text("${hotel.averageRating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Trạng thái duyệt
                val statusColor = when(hotel.status) {
                    "APPROVED" -> Color(0xFF004D40)
                    "REJECTED" -> Color.Red
                    else -> Color(0xFFFF9800) // PENDING
                }
                val statusText = when(hotel.status) {
                    "APPROVED" -> "HOẠT ĐỘNG"
                    "REJECTED" -> "BỊ TỪ CHỐI"
                    else -> "CHỜ DUYỆT"
                }

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.BottomStart)
                ) {
                    Text(
                        statusText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003339))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(hotel.address, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Giá từ: đ${String.format(Locale.getDefault(), "%,.0f", hotel.price)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005D67)
                    )
                    
                    TextButton(onClick = onClick) {
                        Text("Quản lý phòng >", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
