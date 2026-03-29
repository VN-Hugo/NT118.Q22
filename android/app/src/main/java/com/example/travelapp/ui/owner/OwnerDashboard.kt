package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Bảng màu dựa theo thiết kế
val PrimaryTeal = Color(0xFF005D67)
val DarkTeal = Color(0xFF004048)
val AIGreen = Color(0xFF006D5B)
val BgGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen() {
    Scaffold(
        containerColor = BgGray,
        topBar = { DashboardTopBar() },
        bottomBar = { DashboardBottomNav() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Trạng thái hiện tại
            CurrentStatusSection()

            // 2. Gợi ý từ AI
            AISuggestionBox()

            // 3. Chỉ số quan trọng (Grid 1x2)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), title = "TỔNG ĐẶT PHÒNG", value = "1,284", trend = "+5.2%", Icons.Default.DateRange)
                StatCard(modifier = Modifier.weight(1f), title = "DOANH THU THÁNG", value = "458.2M", trend = "+8.1%", Icons.Default.Person)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Nút tác vụ nhanh
            QuickActionsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Biểu đồ xu hướng (Placeholder)
            TrendChartSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Lịch trình hôm nay
            TodayScheduleSection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgGray),
        title = { Text("The Editorial Concierge", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal) },
        navigationIcon = {
            Box(modifier = Modifier.padding(8.dp).size(36.dp).clip(CircleShape).background(Color.Gray)) {
                // Image Profile
            }
        },
        actions = {
            IconButton(onClick = {}) {
                BadgedBox(badge = { Badge { Text("1") } }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryTeal)
                }
            }
        }
    )
}

@Composable
fun CurrentStatusSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("TRẠNG THÁI HIỆN TẠI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("84%", fontSize = 48.sp, fontWeight = FontWeight.Black, color = PrimaryTeal)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tỷ lệ lấp đầy", fontSize = 18.sp, modifier = Modifier.padding(bottom = 10.dp))
        }
        Text("Hiệu suất phòng đang tăng 12% so với tuần trước.", fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun AISuggestionBox() {
    Surface(
        color = AIGreen,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gợi ý từ AI", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nhu cầu phòng \"Deluxe Suite\" đang tăng cao vào cuối tuần tới. Cân nhắc điều chỉnh giá linh hoạt.", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                Text("Xem phân tích chi tiết", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, trend: String, icon: ImageVector) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Text(trend, fontSize = 12.sp, color = Color(0xFF28A745), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickActionsSection() {
    Surface(color = PrimaryTeal, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(4.dp)) {
            QuickActionButton("Thêm phòng mới", Icons.Default.AddCircle)
            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
            QuickActionButton("Xem đặt phòng", Icons.Default.List)
            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
            QuickActionButton("Báo cáo phân tích", Icons.Default.Person)
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun TodayScheduleSection() {
    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Lịch trình hôm nay", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
            Spacer(modifier = Modifier.height(16.dp))
            ScheduleItem("Nguyễn Huy", "Phòng 402 • 14:00", "CHECK-IN", Icons.Default.Lock)
            ScheduleItem("Phạm Trang", "Phòng 105 • 15:30", "CHECK-IN", Icons.Default.Lock)
            ScheduleItem("Lê Duẩn", "Phòng 301 • 11:00", "CHECK-OUT", Icons.Default.Lock)

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xem tất cả danh sách", color = PrimaryTeal)
            }
        }
    }
}

@Composable
fun ScheduleItem(name: String, detail: String, type: String, icon: ImageVector) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(Color(0xFFE0F2F1), CircleShape), contentAlignment = Alignment.Center) {
            Text(name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = PrimaryTeal)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(detail, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun DashboardBottomNav() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.LocationOn, null) }, label = { Text("Tổng quan") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.LocationOn, null) }, label = { Text("Khách sạn") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.LocationOn, null) }, label = { Text("Đặt phòng") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Cá nhân") })
    }
}

@Composable
fun TrendChartSection() {
    // Đây là phần placeholder cho biểu đồ, trong thực tế bạn có thể dùng thư viện như MPAndroidChart hoặc Compose Chart
    Column {
        Text("Biểu đồ xu hướng", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
        Box(modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text("Biểu đồ cột doanh thu", color = Color.LightGray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    OwnerDashboardScreen()
}