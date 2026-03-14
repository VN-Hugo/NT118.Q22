package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
// 1. Model dữ liệu cho địa điểm
data class Place(
    val name: String,
    val location: String,
    val price: String,
    val rating: String,
    val imageBg: Color // Tạm thời dùng màu, bạn thay bằng ID ảnh sau
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val places = listOf(
        Place("Azure Grand...", "Santorini, Greece", "320k", "4.9", Color(0xFFBBDEFB)),
        Place("Urban Loft...", "New York, USA", "215k", "4.7", Color(0xFFC8E6C9)),
        Place("Snowy Peak...", "Aspen, USA", "450k", "4.8", Color(0xFFFFF9C4)),
        Place("Eco Bali Lodge", "Bali, Indonesia", "180k", "4.6", Color(0xFFFFCCBC))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Menu, contentDescription = null)
            Text("Smart Travel AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) // Giả lập avatar
        }

        // --- Search Bar ---
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Tìm kiếm điểm đến...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF1F3F4),
                focusedContainerColor = Color(0xFFF1F3F4),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // --- Filter Chips ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem("Giá", true)
            FilterChipItem("Đánh giá", false)
            FilterChipItem("Khoảng cách", false)
        }

        // --- Tabs (Hotels, Restaurants, Attractions) ---
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("Khách sạn", "Nhà hàng", "Tham quan")

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFF2196F3),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF2196F3)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFF2196F3) else Color.Gray
                        )
                    }
                )
            }
        }

        // --- Grid Danh sách ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(places) { place ->
                PlaceCard(place)
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF1F3F4),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF2196F3)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, color = if (isSelected) Color(0xFF2196F3) else Color.Black, fontSize = 14.sp)
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF2196F3) else Color.Gray
            )
        }
    }
}

@Composable
fun PlaceCard(place: Place) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                // Background Image giả lập
                Box(modifier = Modifier.fillMaxSize().background(place.imageBg))

                // Nút Save (Bookmark)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(place.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                        Text(place.rating, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                    }
                }
                Text(place.location, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(text = "đ${place.price}", fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Text(text = "/đêm", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExplorePreview() {
    ExploreScreen()
}