package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Bảng màu thương hiệu (Sử dụng private hoặc đặt trong một Object/File theme chung để tránh Conflicting declarations)
private val BrandTealColor = Color(0xFF005D67)
private val BgGrayColor = Color(0xFFF8F9FA)
private val ConfirmedBadgeBlueColor = Color(0xFFE1F5FE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingManagementScreen() {
    Scaffold(
        containerColor = BgGrayColor,
        topBar = {
            TopAppBar(
                title = { Text("Quản lý đặt phòng", fontWeight = FontWeight.Bold, color = BrandTealColor) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandTealColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgGrayColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Subtitle
            Text(
                "Quản lý và cập nhật trạng thái các yêu cầu lưu trú của khách hàng.",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Filter Tabs
            BookingFilterTabs()

            Spacer(modifier = Modifier.height(16.dp))

            // 3. List of Bookings
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BookingItemCard(
                    name = "Lê Nam",
                    phone = "090 123 4567",
                    date = "15 Th09 - 18 Th09, 2023 (3 đêm)",
                    room = "Phòng Deluxe hướng biển (201)",
                    price = "4.500.000 đ",
                    status = "MỚI",
                    isActionable = true
                )

                BookingItemCard(
                    name = "Trần Hoa",
                    phone = "091 888 9999",
                    date = "20 Th09 - 22 Th09, 2023 (2 đêm)",
                    room = "Phòng Suite Hoàng Gia (505)",
                    price = "12.000.000 đ",
                    status = "MỚI",
                    isActionable = true
                )

                BookingItemCard(
                    name = "Hoàng Phan",
                    phone = "088 444 5555",
                    date = "25 Th09 - 26 Th09, 2023 (1 đêm)",
                    room = "Phòng Studio (102)",
                    price = "1.200.000 đ",
                    status = "ĐÃ XÁC NHẬN",
                    isActionable = false
                )

                // 4. AI Suggestion Banner
                AIBookingInsightBanner()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BookingFilterTabs() {
    val tabs = listOf("Sắp tới", "Đang diễn ra", "Đã hoàn thành")
    var selectedTab by remember { mutableStateOf("Sắp tới") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = tab },
                color = if (isSelected) BrandTealColor else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = tab,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BookingItemCard(
    name: String,
    phone: String,
    date: String,
    room: String,
    price: String,
    status: String,
    isActionable: Boolean
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar + Name + Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFECEFF1),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(name.split(" ").map { it.take(1) }.joinToString(""), fontWeight = FontWeight.Bold, color = BrandTealColor)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(phone, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                    }
                }
                // Badge
                Surface(
                    color = if (status == "MỚI") BrandTealColor else ConfirmedBadgeBlueColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (status == "MỚI") Color.White else BrandTealColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BgGrayColor)

            // Details
            BookingDetailRow(Icons.Default.DateRange, date)
            BookingDetailRow(Icons.Default.Home, room)
            BookingDetailRow(Icons.Default.Info, price, isPrice = true)

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (isActionable) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTealColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Chấp nhận", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Từ chối", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Xem chi tiết", color = BrandTealColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(icon: ImageVector, text: String, isPrice: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, null, tint = BrandTealColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 13.sp,
            color = if (isPrice) BrandTealColor else Color.DarkGray,
            fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal,
            textDecoration = if (isPrice) androidx.compose.ui.text.style.TextDecoration.Underline else null
        )
    }
}

@Composable
fun AIBookingInsightBanner() {
    Surface(
        color = BrandTealColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gợi ý từ Trí tuệ Nhân tạo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Dựa trên xu hướng đặt phòng, \"Phòng Deluxe hướng biển\" đang có nhu cầu cao cho cuối tuần tới. Bạn nên xem xét điều chỉnh giá tăng 10% để tối ưu lợi nhuận.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookingPreview() {
    BookingManagementScreen()
}
