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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandTealColor = Color(0xFF005D67)
private val BgGrayColor = Color(0xFFF8F9FA)
private val ConfirmedBadgeBlueColor = Color(0xFFE1F5FE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingManagementScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGrayColor)
    ) {
        // TopBar riêng cho màn hình
        TopAppBar(
            title = { Text("Quản lý đặt phòng", fontWeight = FontWeight.Bold, color = BrandTealColor) },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandTealColor)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BgGrayColor)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Quản lý và cập nhật trạng thái các yêu cầu lưu trú của khách hàng.",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))
            BookingFilterTabs()
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BookingItemCard("Lê Nam", "090 123 4567", "15 Th09 - 18 Th09, 2023", "Phòng Deluxe (201)", "4.500.000 đ", "MỚI", true)
                BookingItemCard("Trần Hoa", "091 888 9999", "20 Th09 - 22 Th09, 2023", "Phòng Suite (505)", "12.000.000 đ", "MỚI", true)
                BookingItemCard("Hoàng Phan", "088 444 5555", "25 Th09 - 26 Th09, 2023", "Phòng Studio (102)", "1.200.000 đ", "ĐÃ XÁC NHẬN", false)
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
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Surface(
                modifier = Modifier.weight(1f).clickable { selectedTab = tab },
                color = if (isSelected) BrandTealColor else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(tab, modifier = Modifier.padding(vertical = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BookingItemCard(name: String, phone: String, date: String, room: String, price: String, status: String, isActionable: Boolean) {
    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(Color(0xFFECEFF1), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text(name.take(1), fontWeight = FontWeight.Bold, color = BrandTealColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(phone, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(color = if (status == "MỚI") BrandTealColor else ConfirmedBadgeBlueColor, shape = RoundedCornerShape(8.dp)) {
                    Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = if (status == "MỚI") Color.White else BrandTealColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BgGrayColor)
            BookingDetailRow(Icons.Default.DateRange, date)
            BookingDetailRow(Icons.Default.Home, room)
            BookingDetailRow(Icons.Default.Info, price, true)
            if (isActionable) {
                Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BrandTealColor)) { Text("Chấp nhận") }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Từ chối", color = Color.Red) }
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(icon: ImageVector, text: String, isPrice: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, null, tint = BrandTealColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = if (isPrice) BrandTealColor else Color.DarkGray, fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun AIBookingInsightBanner() {
    Surface(color = BrandTealColor, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(" Gợi ý AI", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text("Nhu cầu đang tăng cao, hãy tối ưu giá phòng của bạn.", color = Color.White.copy(0.9f), fontSize = 13.sp)
        }
    }
}
