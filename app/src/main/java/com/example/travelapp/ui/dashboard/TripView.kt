package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
// 1. Data Models cho chuyến đi
data class Trip(
    val title: String,
    val date: String,
    val desc: String,
    val status: String, // CONFIRMED, PROCESSING
    val statusColor: Color,
    val imageBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen() {
    val upcomingTrips = listOf(
        Trip("Summer in Kyoto", "12 Th8 - 18 Th8, 2024", "5 ngày: Rừng tre Arashiyama, Chùa Vàng Kinkaku-ji...", "ĐÃ XÁC NHẬN", Color(0xFF2196F3), Color(0xFFE8F5E9)),
        Trip("Santorini Escape", "05 Th9 - 10 Th9, 2024", "4 đêm tại Oia: Ngắm hoàng hôn, thử rượu vang...", "ĐANG XỬ LÝ", Color(0xFF90CAF9), Color(0xFFFFF3E0))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Travel AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.padding(start = 16.dp).size(32.dp))
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // --- Tab Selector ---
            item {
                TripTabs()
            }

            // --- Upcoming Trips Header ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chuyến đi sắp tới", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(12.dp)) {
                        Text("2 Đang hoạt động", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF2196F3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- Trip Cards ---
            items(upcomingTrips) { trip ->
                TripCard(trip)
            }

            // --- Plan with AI Banner ---
            item {
                AIBanner()
            }

            // --- Saved Collections ---
            item {
                SectionHeader(title = "Bộ sưu tập đã lưu", action = "Xem tất cả")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    item { CollectionCard("Swiss Alps", "12 bản thảo AI", Color(0xFFBBDEFB)) }
                    item { CollectionCard("Cultural India", "5 bản thảo AI", Color(0xFFFFCCBC)) }
                }
            }

            // --- View Past Journeys ---
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray)
                        Column(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
                            Text("Xem hành trình cũ", fontWeight = FontWeight.Bold)
                            Text("Xem lại 14 chuyến đi trước đó", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.LightGray)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun TripTabs() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sắp tới", "Đã lưu", "Đã đi")
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Color(0xFF2196F3)
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = { Text(title, color = if (selectedTab == index) Color(0xFF2196F3) else Color.Gray, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp).fillMaxWidth().background(trip.imageBg))
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text(trip.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Surface(color = trip.statusColor, shape = RoundedCornerShape(8.dp)) {
                        Text(trip.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp), // Phải bọc trong Modifier
                        tint = Color.Gray
                    )
                    Text(trip.date, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                }
                Text(trip.desc, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text(if (trip.status == "ĐANG XỬ LÝ") "Hoàn tất đặt chỗ" else "Xem lịch trình")
                    }
                    IconButton(onClick = {}, modifier = Modifier.background(Color(0xFFF1F5F9), CircleShape)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun AIBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF2196F3)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("Lên kế hoạch với AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Hãy để Smart Travel AI tạo chuyến đi mơ ước của bạn.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Bắt đầu kế hoạch mới", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CollectionCard(title: String, subtitle: String, color: Color) {
    Card(modifier = Modifier.size(160.dp, 200.dp), shape = RoundedCornerShape(20.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(color)) {
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Color(0xFF2196F3), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun TripsPreview() {
    MaterialTheme { MyTripsScreen() }
}