package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Màu sắc theo thiết kế
private val TealDark = Color(0xFF004D40)
private val BgLightGray = Color(0xFFF8F9FA)
private val AiCardBg = Color(0xFFE0F2F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onNavigate: (String) -> Unit
) {
    // KHÔNG dùng HotelBottomBar ở đây nữa vì đã có ở OwnerDashboardContainer
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("The Editorial Concierge", fontWeight = FontWeight.Bold, color = TealDark) },
            actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null) } },
            navigationIcon = {
                Box(Modifier.padding(8.dp).size(35.dp).background(Color.Gray, CircleShape))
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. Tổng quan phản hồi
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("TỔNG QUAN PHẢN HỒI", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("4.8", fontSize = 60.sp, fontWeight = FontWeight.Black, color = TealDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(4) { Icon(Icons.Default.Star, null, tint = TealDark) }
                        Icon(Icons.Default.Lock, null, tint = TealDark)
                    }
                    Text("Dựa trên 1,248 đánh giá", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 2. AI Insight Card
            item {
                Surface(color = AiCardBg, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(TealDark, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Insight: Sự hài lòng cao", fontWeight = FontWeight.Bold, color = TealDark)
                            Text(
                                "Khách hàng thường xuyên khen ngợi về \"dịch vụ concierge tận tâm\"...",
                                fontSize = 13.sp, color = TealDark.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // 3. Phân phối xếp hạng
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Phân phối xếp hạng", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        RatingBarRow(5, 0.85f, "85%")
                        RatingBarRow(4, 0.10f, "10%")
                        RatingBarRow(3, 0.03f, "3%")
                        RatingBarRow(2, 0.01f, "1%")
                        RatingBarRow(1, 0.01f, "1%")
                    }
                }
            }

            // 4. Review Items
            item {
                ReviewItem(
                    name = "Lê Thu Hà",
                    info = "Đã lưu trú • 2 đêm • Phòng Suite",
                    time = "2 ngày trước",
                    content = "Trải nghiệm tuyệt vời tại khách sạn! Nhân viên concierge đã hỗ trợ tôi đặt chỗ tại nhà hàng Michelin...",
                    hasImages = true,
                    reply = "Chào chị Hà, cảm ơn chị đã dành thời gian đánh giá..."
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun RatingBarRow(star: Int, progress: Float, percent: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(star.toString(), fontSize = 12.sp, modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
            color = TealDark,
            trackColor = Color(0xFFEEEEEE)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(percent, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(30.dp))
    }
}

@Composable
fun ReviewItem(name: String, info: String, time: String, content: String, hasImages: Boolean, reply: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color.LightGray, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(info, fontSize = 11.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row { repeat(5) { Icon(Icons.Default.Star, null, tint = TealDark, modifier = Modifier.size(16.dp)) } }
                Text(time, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Text(content, fontSize = 14.sp, modifier = Modifier.padding(vertical = 12.dp))

        if (hasImages) {
            Row(modifier = Modifier.height(100.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f).fillMaxHeight().background(Color.LightGray, RoundedCornerShape(8.dp)))
                Box(Modifier.weight(1f).fillMaxHeight().background(Color.DarkGray, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text("+2 ảnh", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (reply != null) {
            Box(modifier = Modifier.padding(top = 12.dp).background(BgLightGray, RoundedCornerShape(8.dp)).padding(12.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(" Bạn đã phản hồi:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealDark)
                    }
                    Text(reply, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
