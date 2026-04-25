package com.example.travelapp.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.ui.components.HotelBottomBar

// 1. Màu sắc chủ đạo từ thiết kế
private val PrimaryDark = Color(0xFF004D40)
private val BgtGray = Color(0xFFF8F9FA)
private val StatusGreen = Color(0xFFE0F2F1)
private val TextGreen = Color(0xFF00796B)
private val AiBg = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đặt phòng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDark) },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = null) } }
            )
        },
        bottomBar = {
            HotelBottomBar(currentRoute = "bookings", onNavigate = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BgtGray)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Header: Mã đặt phòng
            item {
                Column {
                    Text("MÃ ĐẶT PHÒNG", fontSize = 12.sp, color = Color.Gray)
                    Text("#EC-782910", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = StatusGreen, shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextGreen, modifier = Modifier.size(16.dp))
                            Text(" Trạng thái: Đã xác nhận", color = TextGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section: Thông tin khách hàng
            item {
                InfoCard(title = "Thông tin khách hàng", icon = Icons.Default.Person) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).background(Color.LightGray, CircleShape)) // Giả lập Avatar
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Nguyễn Minh Tuấn", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("tuanm.contact@email.com", fontSize = 13.sp, color = Color.Gray)
                            Text("+84 901 234 567", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Text("  Liên hệ khách", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section: Chi tiết lưu trú
            item {
                InfoCard(title = "Chi tiết lưu trú") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("NGÀY NHẬN PHÒNG", fontSize = 10.sp, color = Color.Gray)
                            Text("15 Th05, 2024", fontWeight = FontWeight.Bold)
                            Text("Từ 14:00", fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("NGÀY TRẢ PHÒNG", fontSize = 10.sp, color = Color.Gray)
                            Text("18 Th05, 2024", fontWeight = FontWeight.Bold)
                            Text("Trước 12:00", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(color = BgGray, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Phòng Deluxe View Biển", fontWeight = FontWeight.Bold)
                                Text("2 Người lớn • 1 Trẻ em", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("#302", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }

            // Section: Gợi ý AI
            item {
                Surface(color = AiBg, shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextGreen, modifier = Modifier.size(18.dp))
                            Text(" Gợi ý từ AI", color = TextGreen, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "Khách hàng này thường yêu cầu gối thêm và check-in sớm. Hãy chuẩn bị phòng 302 trước 13:00...",
                            fontSize = 13.sp, color = PrimaryDark, modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = {}, label = { Text("Khách VIP") })
                            AssistChip(onClick = {}, label = { Text("Trung thành") })
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun InfoCard(title: String, icon: ImageVector? = null, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                icon?.let { Icon(it, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
