package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen() {
    Scaffold(
        bottomBar = { BookingBottomBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Image Section
            HeaderImageSection()

            Column(modifier = Modifier.padding(16.dp)) {
                // 2. Title & Price
                TitleAndPriceSection()

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text("Santorini, Hy Lạp", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Rating Bar
                RatingSection()

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Amenities
                Text("Tiện ích hàng đầu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                AmenitiesRow()

                Spacer(modifier = Modifier.height(24.dp))

                // 6. About Section
                Text("Về khách sạn này", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Trải nghiệm dịch vụ đẳng cấp thế giới tại Aura Grand Resort & Spa. Tọa lạc trên vách đá Oia, điểm đến được hỗ trợ bởi AI này cung cấp hệ thống điều khiển phòng thông minh...",
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Text("Xem thêm", color = Color(0xFF007BFF), fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Map Preview (Placeholder)
                Text("Vị trí", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bản đồ vị trí")
                }
            }
        }
    }
}

@Composable
fun HeaderImageSection() {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        // Thay bằng Image thực tế của bạn
        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))

        // Nút điều hướng phía trên
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Row {
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun TitleAndPriceSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Aura Grand Resort & \nSpa",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text("$420", color = Color(0xFF007BFF), fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("mỗi đêm", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RatingSection() {
    Surface(
        color = Color(0xFFE3F2FD),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("4.9", fontWeight = FontWeight.Bold, color = Color(0xFF007BFF), fontSize = 18.sp)
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF007BFF), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Tuyệt vời", fontWeight = FontWeight.Bold)
                Text("Dựa trên 2,450 đánh giá", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun AmenitiesRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        AmenityIcon(Icons.Default.Star, "Wi-Fi")
        AmenityIcon(Icons.Default.Build, "Hồ bơi")
        AmenityIcon(Icons.Default.CheckCircle, "Gym")
        AmenityIcon(Icons.Default.Face, "Spa")
    }
}

@Composable
fun AmenityIcon(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF007BFF))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = Color.DarkGray)
    }
}

@Composable
fun BookingBottomBar() {
    Surface(shadowElevation = 8.dp) {
        Button(
            onClick = { /* Handle booking */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đặt phòng ngay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HotelDetailsPreview() {
    MaterialTheme {
        HotelDetailsScreen()
    }
}