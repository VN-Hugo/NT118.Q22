package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.data.model.Review
import java.text.SimpleDateFormat
import java.util.*

private val BrandTealReview = Color(0xFF005D67)
private val ReviewScreenBgGray = Color(0xFFF8F9FA)
private val AiCardBgReview = Color(0xFFE0F2F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHotelPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ReviewScreenBgGray,
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.clickable { showHotelPicker = true }) {
                        val hotelName = when (val state = uiState) {
                            is ReviewManagementState.Success -> state.selectedHotel?.name ?: "Chọn khách sạn"
                            else -> "Đang tải..."
                        }
                        Text(hotelName, fontWeight = FontWeight.Bold, color = BrandTealReview, fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Chạm để đổi khách sạn", fontSize = 11.sp, color = Color.Gray)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        }
                    }
                },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = BrandTealReview) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ReviewManagementState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = BrandTealReview) }
            }
            is ReviewManagementState.Success -> {
                ReviewContent(padding, state)
                
                if (showHotelPicker) {
                    HotelSelectionDialog(
                        hotels = state.hotels,
                        onDismiss = { showHotelPicker = false },
                        onSelect = { 
                            viewModel.selectHotel(it)
                            showHotelPicker = false
                        }
                    )
                }
            }
            is ReviewManagementState.Error -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message, color = Color.Red) }
            }
        }
    }
}

@Composable
fun ReviewContent(padding: PaddingValues, state: ReviewManagementState.Success) {
    val hotel = state.selectedHotel
    val reviews = state.reviews

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // 1. Chỉ số Rating tổng quát
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ĐIỂM ĐÁNH GIÁ TRUNG BÌNH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", hotel?.averageRating ?: 0f),
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = BrandTealReview
                    )
                    RatingStars(hotel?.averageRating ?: 0f)
                    Text("Dựa trên ${hotel?.reviewCount ?: 0} nhận xét", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // 2. AI Insight
        item {
            Surface(color = AiCardBgReview, shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.size(36.dp).background(BrandTealReview, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Trợ lý AI phân tích", fontWeight = FontWeight.Bold, color = BrandTealReview, fontSize = 14.sp)
                        val insight = if ((hotel?.averageRating ?: 0f) >= 4.0f) {
                            "Khách hàng rất hài lòng! Điểm mạnh là sự chuyên nghiệp. Hãy tiếp tục phát huy."
                        } else {
                            "Có một số phản hồi chưa tốt. Bạn nên kiểm tra lại quy trình phục vụ hoặc vệ sinh phòng."
                        }
                        Text(insight, fontSize = 13.sp, color = BrandTealReview.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // 3. Danh sách đánh giá chi tiết
        item {
            Text("Nhận xét từ khách hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        }

        if (reviews.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), Alignment.Center) {
                    Text("Chưa có đánh giá nào", color = Color.Gray)
                }
            }
        } else {
            items(reviews) { review ->
                ReviewListItem(review)
            }
        }
        
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun RatingStars(rating: Float) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        repeat(5) { index ->
            val isSelected = index < rating.toInt()
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReviewListItem(review: Review) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (review.userAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = review.userAvatar,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.size(36.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(review.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(sdf.format(Date(review.createdAt)), fontSize = 11.sp, color = Color.Gray)
                }
                Row {
                    repeat(review.rating) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(review.comment, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}

@Composable
fun HotelSelectionDialog(hotels: List<com.example.travelapp.data.model.Property>, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn khách sạn", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn {
                items(hotels) { hotel ->
                    TextButton(
                        onClick = { onSelect(hotel.proId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(hotel.name, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}
