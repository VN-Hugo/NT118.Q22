package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Sử dụng private để tránh Conflicting declarations với các file khác
private val BrandTealColor = Color(0xFF005D67)
private val SoftTealBgColor = Color(0xFFE0F2F1)
private val StatusGreenColor = Color(0xFF28A745)
private val StatusOrangeColor = Color(0xFFFD7E14)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen() {
    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        // bottomBar = { DashboardBottomNav() } // Tạm thời comment nếu chưa import được DashboardBottomNav
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. Header Section
            Text("QUẢN LÝ HỆ THỐNG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("Quản lý phòng", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandTealColor)
            Text("Điều chỉnh trạng thái, giá cả và thông tin các hạng phòng trong bộ sưu tập của bạn.", fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Add room logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = BrandTealColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm phòng mới", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. AI Suggestion Card
            AISuggestionBanner()

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Category: Deluxe Collection
            CategoryHeader(title = "Bộ sưu tập Deluxe", count = 4)
            RoomCard(
                title = "Deluxe Garden View - 301",
                price = "2.450.000đ",
                specs = "35m² • 2 người",
                status = "SẴN SÀNG",
                statusColor = StatusGreenColor
            )
            RoomCard(
                title = "Deluxe City View - 302",
                price = "2.750.000đ",
                specs = "38m² • 2 người",
                status = "SẴN SÀNG",
                statusColor = StatusGreenColor
            )
            RoomCard(
                title = "Deluxe Twin - 305",
                price = "2.200.000đ",
                specs = "38m² • 2 người",
                status = "ĐANG BẢO TRÌ",
                statusColor = StatusOrangeColor,
                isAvailable = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Category: Signature Suite
            CategoryHeader(title = "Signature Suite", count = 2)
            RoomCard(
                title = "Presidential Ocean Suite - 501",
                price = "12.500.000đ",
                specs = "120m² • 4 người",
                status = "SẴN SÀNG",
                statusColor = StatusGreenColor
            )

            // 5. Add New Placeholder
            AddRoomPlaceholder(category = "Signature Suite")

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AISuggestionBanner() {
    Surface(
        color = SoftTealBgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(BrandTealColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White) // Đổi Lightbulb sang Info nếu thiếu
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Gợi ý AI: Hạng phòng Deluxe đang có nhu cầu cao vào cuối tuần tới. Cân nhắc điều chỉnh giá tăng 15% để tối ưu doanh thu.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
            TextButton(onClick = {}) {
                Text("ÁP DỤNG NGAY", color = BrandTealColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CategoryHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandTealColor)
        Text("$count Phòng", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun RoomCard(title: String, price: String, specs: String, status: String, statusColor: Color, isAvailable: Boolean = true) {
    var isVisible by remember { mutableStateOf(isAvailable) }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            // Room Image with Overlays
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                // Placeholder Image
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))

                // Status Badge
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Edit/Delete Buttons
                Row(modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(Color.White, CircleShape)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(Color.White, CircleShape)) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.Red)
                    }
                }
            }

            // Room Info
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("GIÁ MỖI ĐÊM", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(price, fontWeight = FontWeight.Bold, color = BrandTealColor, fontSize = 16.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(14.dp)) // Đổi SquareFoot sang Info
                    Text(specs, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F1F1))

                // Visibility Toggle
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Trạng thái hiển thị", fontSize = 12.sp, color = Color.Gray)
                    Switch(
                        checked = isVisible,
                        onCheckedChange = { isVisible = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = BrandTealColor)
                    )
                }
            }
        }
    }
}

@Composable
fun AddRoomPlaceholder(category: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.fillMaxWidth().height(160.dp).clickable { }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Thêm phòng mới cho $category", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                "Mở rộng bộ sưu tập cao cấp của bạn với các không gian nghệ thuật mới.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "Tạo ngay",
                color = BrandTealColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RoomManagementPreview() {
    RoomManagementScreen()
}
