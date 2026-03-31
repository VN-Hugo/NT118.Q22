package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Hotel(
    val name: String,
    val location: String,
    val rating: Double,
    val status: String,
    val isActive: Boolean = true,
    val staffCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelManagementScreen() {
    val hotelList = listOf(
        Hotel("The Azure Palace", "Đà Nẵng, Việt Nam", 4.9, "HOẠT ĐỘNG", true, 5),
        Hotel("Heritage Grand Hotel", "Hà Nội, Việt Nam", 4.7, "HOẠT ĐỘNG", true, 12),
        Hotel("Urban Vista Suites", "TP. Hồ Chí Minh, Việt Nam", 4.2, "NGỪNG HOẠT ĐỘNG", false, 0),
        Hotel("The Serene Cove", "Phú Quốc, Việt Nam", 5.0, "HOẠT ĐỘNG", true, 2)
    )

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Điều hướng tới màn hình Thêm khách sạn */ },
                containerColor = Color(0xFF005D67), // BrandTeal
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm khách sạn mới", fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = { DashboardBottomNav() }
    ) { innerPadding ->
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
                AIInsightBanner()
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(hotelList) { hotel ->
                HotelManagementCard(hotel)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun AIInsightBanner() {
    Surface(
        color = Color(0xFF005D67),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("AI Insight", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "Tỷ lệ lấp đầy trung bình tăng 12% so với tháng trước tại các cơ sở ven biển.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HotelManagementCard(hotel: Hotel) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Column {
            // Phần ảnh phía trên
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                // Placeholder Image (Dùng Box màu xám nếu chưa có ảnh thực)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (hotel.isActive) Color.LightGray else Color(0xFFE0E0E0))
                )

                // Rating Badge
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                        Text("${hotel.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Status Badge
                Surface(
                    color = if (hotel.isActive) Color(0xFF004D40) else Color(0xFF757575),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.BottomStart)
                ) {
                    Text(
                        hotel.status,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Phần thông tin phía dưới
            Column(modifier = Modifier.padding(16.dp)) {
                Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003339))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(hotel.location, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Staff Avatars (Simulated)
                    Row {
                        repeat(minOf(hotel.staffCount, 3)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2F1))
                            )
                        }
                        if (hotel.staffCount > 3) {
                            Text("+${hotel.staffCount - 3}", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
                        }
                    }

                    // Action Button
                    TextButton(onClick = {}) {
                        Text(
                            if (hotel.isActive) "Chi tiết >" else "Quản lý ⚙",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HotelManagementPreview() {
    HotelManagementScreen()
}