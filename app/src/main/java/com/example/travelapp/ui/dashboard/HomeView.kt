
package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
// Import Component bạn đã tạo ở file kia
import com.example.travelapp.ui.components.TravelBottomBar

@Preview(showBackground = true, showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartTravelHomeScreen() {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            // Gọi Component đã tách ở đây
            TravelBottomBar(
                currentRoute = "home",
                onNavigate = { route ->
                    // Logic chuyển màn hình sẽ viết ở đây sau
                    println("Navigate to $route")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color.White)
        ) {
            SearchBar()
            FeaturedCard()
            SectionHeader(title = "Suggested Hotels", hasSeeAll = true)
            HotelList()
            SectionHeader(title = "Limited Time Deals", hasSeeAll = false)
            DealsList()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Các UI nhỏ giữ nguyên bên dưới ---

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF1976D2))
        Text("Smart Travel AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search destinations, hotels...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun FeaturedCard() {
    Box(
        modifier = Modifier.padding(16.dp).fillMaxWidth().height(220.dp).clip(RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp)) {
                Text("FEATURED DESTINATION", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
            Text("Discover the Magic of\nGreece", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionHeader(title: String, hasSeeAll: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (hasSeeAll) Text("See all", color = Color(0xFF1976D2), fontSize = 14.sp)
    }
}

@Composable
fun HotelList() {
    val hotels = listOf(
        Hotel("Azure Bay Resort", "Santorini, Greece", "$240", "4.9"),
        Hotel("The Urban Loft", "Athens, Greece", "$185", "4.7")
    )
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(hotels) { hotel ->
            Card(
                modifier = Modifier.width(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.LightGray))
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text(hotel.name, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                Text(hotel.rating, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Text(hotel.location, fontSize = 12.sp, color = Color.Gray)
                        Text("${hotel.price}/night", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DealsList() {
    val deals = listOf(
        Deal("Italian Riviera Escape", "Save 30% on 5-night bookings", "-30%", Color(0xFF1976D2)),
        Deal("Kyoto Zen Experience", "AI-planned custom itinerary", "Free AI", Color(0xFF1976D2))
    )
    deals.forEach { deal ->
        Surface(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            color = Color.White
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray))
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(deal.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(deal.desc, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(color = deal.color, shape = RoundedCornerShape(16.dp)) {
                    Text(deal.tag, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Data class (Nếu bạn chưa tách file thì để tạm ở đây)
data class Hotel(val name: String, val location: String, val price: String, val rating: String)
data class Deal(val title: String, val desc: String, val tag: String, val color: Color)