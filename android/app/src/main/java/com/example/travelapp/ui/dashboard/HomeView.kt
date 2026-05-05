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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.data.model.Property
import java.util.Locale

data class Deal(val title: String, val desc: String, val tag: String, val color: Color)

@Composable
fun SmartTravelHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onPropertyClick: (String) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()

    val properties = when (homeState) {
        is HomeState.Success -> (homeState as HomeState.Success).suggestedHotels
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        TopBar()
        SearchBar()
        FeaturedCard()

        SectionHeader(title = "Gợi ý cho bạn", hasSeeAll = true)

        PropertyList(properties = properties, onPropertyClick = onPropertyClick)

        SectionHeader(title = "Ưu đãi đặc biệt", hasSeeAll = false)
        DealsList()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF1976D2))
        Text("Smart Travel AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { /* Handle notifications */ }) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Bạn muốn đi đâu?", color = Color.Gray) },
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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Ảnh nền mẫu về du lịch Việt Nam
        AsyncImage(
            model = "https://images.unsplash.com/photo-1528127269322-539801943592?q=80&w=2070&auto=format&fit=crop",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Overlay để chữ dễ đọc hơn
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ĐIỂM ĐẾN NỔI BẬT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            Text(
                text = "Khám phá vẻ đẹp kỳ ảo\ncủa Vịnh Hạ Long",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, hasSeeAll: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (hasSeeAll) {
            Text("Xem tất cả", color = Color(0xFF1976D2), fontSize = 14.sp, modifier = Modifier.clickable { })
        }
    }
}

@Composable
fun PropertyList(properties: List<Property>, onPropertyClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(properties) { property ->
            Card(
                modifier = Modifier
                    .width(220.dp)
                    .clickable { onPropertyClick(property.proId) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    val imageUrl = property.images.firstOrNull { it.isPrimary }?.url ?: property.images.firstOrNull()?.url
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.height(140.dp).fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.LightGray))
                    }
                    
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(property.name, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                Text(String.format("%.1f", property.averageRating), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Text(property.address, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                        
                        if (property.price > 0) {
                            Text(
                                text = "Từ đ${String.format(Locale.getDefault(), "%,.0f", property.price)} / đêm",
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DealsList() {
    val deals = listOf(
        Deal("Kỳ nghỉ Đà Lạt", "Giảm 30% khi đặt từ 2 đêm", "-30%", Color(0xFFE91E63)),
        Deal("Phú Quốc rực rỡ", "AI lên lịch trình tham quan miễn phí", "Free AI", Color(0xFF1976D2))
    )
    deals.forEach { deal ->
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            color = Color.White
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFBBDEFB))) {
                    Icon(Icons.Default.CardGiftcard, null, modifier = Modifier.align(Alignment.Center), tint = Color(0xFF1976D2))
                }
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

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    SmartTravelHomeScreen(onPropertyClick = {})
}
