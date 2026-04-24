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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelapp.data.model.Property

// LƯU Ý: Bạn hãy import PropertyRepository theo đúng đường dẫn trong app của bạn nhé!
// Ví dụ: import com.example.travelapp.domain.repository.PropertyRepository
// Ở đây mình tạm comment lại để tránh báo đỏ nếu sai đường dẫn
// import com.example.travelapp.repository.PropertyRepository

data class Deal(val title: String, val desc: String, val tag: String, val color: Color)

//class HomeViewModel : ViewModel() {
//    // 1. Khởi tạo kho dữ liệu mới (Sửa lại tên repository cho đúng với file của bạn)
//    // private val repository = PropertyRepository()
//
//    // 2. Chuyển từ List<Hotel> sang List<Property>
//    var propertyList = mutableStateOf<List<Property>>(emptyList())
//        private set
//
//    // 3. Hàm gọi dữ liệu
//    fun loadProperties() {
//        // LƯU Ý: Bỏ comment và gọi đúng hàm lấy danh sách trong PropertyRepository của bạn
//        /*
//        repository.getAllProperties(
//            onSuccess = { data ->
//                propertyList.value = data
//            },
//            onFailure = { exception ->
//                // Xử lý báo lỗi ở đây nếu cần
//            }
//        )
//        */
//
//        // Tạm thời tạo data giả (Mock Data) để bạn thấy UI không bị lỗi:
//        propertyList.value = listOf(
//            Property(name = "Khách sạn Mường Thanh", address = "Đà Nẵng", averageRating = 4.5f),
//            Property(name = "Vinpearl Resort", address = "Nha Trang", averageRating = 4.8f)
//        )
//    }
//}

@Composable
fun SmartTravelHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPropertyClick: (String) -> Unit
) {
    // 1. Lắng nghe toàn bộ trạng thái từ ViewModel
    val homeState by viewModel.homeState.collectAsState()

// 2. Tự động bóc tách danh sách từ trạng thái Success
    val properties = when (homeState) {
        is HomeState.Success -> (homeState as HomeState.Success).suggestedHotels
        else -> emptyList() // Trả về danh sách rỗng nếu đang Loading hoặc bị Error
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

        SectionHeader(title = "Suggested Properties", hasSeeAll = true)

        // Truyền dữ liệu mới vào
        PropertyList(properties = properties)

        SectionHeader(title = "Limited Time Deals", hasSeeAll = false)
        DealsList()

        Spacer(modifier = Modifier.height(24.dp)) // Tạo khoảng trống cuối trang
    }
}

// --- Thành phần UI nhỏ ---

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
        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray)
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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Tạm thời dùng màu xám, sau này bạn thay bằng Image()
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "FEATURED DESTINATION",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            Text(
                text = "Discover the Magic of\nGreece",
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
            Text("See all", color = Color(0xFF1976D2), fontSize = 14.sp)
        }
    }
}

@Composable
fun PropertyList(properties: List<Property>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(properties) { property ->
            Card(
                modifier = Modifier.width(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.LightGray))
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            // Gọi property.name
                            Text(property.name, fontWeight = FontWeight.Bold, maxLines = 1)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                // Ép kiểu float thành string cho averageRating
                                Text(property.averageRating.toString(), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        // Đổi location thành address
                        Text(property.address, fontSize = 12.sp, color = Color.Gray)

                        // Đã ẩn phần giá tiền vì Property hiện tại không lưu giá cơ sở
                        // Text("Đang cập nhật/night", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
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
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    SmartTravelHomeScreen(onPropertyClick = {})
}