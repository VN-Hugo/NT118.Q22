package com.example.travelapp.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.R // Hãy đảm bảo đúng package của dự án bạn
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen() {
    // Scaffold cung cấp khung cơ bản cho ứng dụng (TopBar, BottomBar)
    Scaffold(
        bottomBar = { TravelBottomBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F6F8)) // Nền xám nhạt
        ) {
            // 1. Thanh tìm kiếm và các Tab địa danh (Phần màu xanh)
            HeaderSection()

            // 2. Menu chính: Khách sạn, Vé máy bay... (Dùng icon từ drawable)
            MainCategoryGrid()

            // 3. Phần Ưu đãi cho người mới
            NewUserOfferSection()

            // 4. Các Banner khuyến mãi (Trung Quốc, Visa, Cửa Lò...)
            PromotionBannerSection()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2B54E0)) // Màu xanh chủ đạo
            .padding(16.dp)
    ) {
        // Thanh Search trắng bo tròn
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Text(" Luân Đôn", color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danh sách tag địa danh (Rome, Las Vegas...)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val cities = listOf("Luân Đôn", "Rome", "Las Vegas", "Osaka", "Thượng Hải")
            cities.forEach { city ->
                Text(
                    text = city,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = if (city == "Luân Đôn") FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun MainCategoryGrid() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 20.dp)) {
            // Hàng 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                CategoryItem(R.drawable.ic_bed, "Khách sạn")
                CategoryItem(R.drawable.ic_bed, "Vé máy bay")
                CategoryItem(R.drawable.ic_car, "Combo Tiết Kiệm")
                CategoryItem(R.drawable.ic_car, "Vé tàu")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hàng 2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                CategoryItem(R.drawable.ic_car, "Nhà & Căn Hộ")
                CategoryItem(R.drawable.ic_bed, "Tour & Hoạt động")
                CategoryItem(R.drawable.ic_bed, "Đưa đón sân bay")
                CategoryItem(R.drawable.ic_car, "+2 mục khác")
            }
        }
    }
}

@Composable
fun CategoryItem(iconRes: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(42.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 14.sp
        )
    }
}

@Composable
fun NewUserOfferSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_car), // Thay bằng icon quà của bạn
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text("Ưu đãi cho người dùng mới", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("đang chờ bạn", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = { /* Nhận ưu đãi */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B54E0)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Nhận Tất Cả", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun PromotionBannerSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner Trái (Ví dụ: Đi Trung Quốc)
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.background), // Dùng ảnh nền núi của bạn
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(4.dp))
                ) {
                    Text("Thứ 3 hàng tuần", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }

        // Cột bên phải (Visa và Cửa Lò)
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner Visa
            Box(modifier = Modifier.weight(1.2f).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF001E5D))) {
                Text("FIFA WORLD CUP 26", color = Color.White, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            // Banner Cửa Lò
            Box(modifier = Modifier.weight(0.8f).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFE3F2FD))) {
                Text("Khám phá Cửa Lò", color = Color(0xFF1976D2), modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Composable
fun TravelBottomBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp // Sửa từ 'tone' thành 'tonal'
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_apple),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Trang chủ") },
            // Optional: Chỉnh màu khi được chọn
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2B54E0),
                indicatorColor = Color(0xFFE3F2FD) // Màu nền của icon khi được chọn
            )
        )
        // ... Các item khác giữ nguyên ...
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(painterResource(id = R.drawable.ic_car), null, Modifier.size(24.dp)) },
            label = { Text("Tin nhắn") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(painterResource(id = R.drawable.ic_apple), null, Modifier.size(24.dp)) },
            label = { Text("Chuyến đi") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(painterResource(id = R.drawable.ic_bed), null, Modifier.size(24.dp)) },
            label = { Text("Đăng nhập") }
        )
    }
}