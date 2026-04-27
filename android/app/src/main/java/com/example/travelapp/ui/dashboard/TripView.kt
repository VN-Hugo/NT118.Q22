package com.example.travelapp.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.data.model.Booking
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    onBookingClick: (String) -> Unit,
    viewModel: TripViewModel = hiltViewModel()
) {
    val tripState by viewModel.tripState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sắp tới", "Đã lưu", "Đã đi")
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chuyến đi của tôi", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            when (val state = tripState) {
                is TripState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2196F3))
                    }
                }
                is TripState.Success -> {
                    val currentList = when (selectedTabIndex) {
                        0 -> state.upcoming
                        1 -> state.saved
                        else -> state.past
                    }

                    if (currentList.isEmpty()) {
                        EmptyTripState(tabs[selectedTabIndex])
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(currentList, key = { it.bookId }) { booking ->
                                BookingTripCard(
                                    booking = booking,
                                    onClick = { onBookingClick(booking.proId) },
                                    onCancel = {
                                        viewModel.cancelBooking(booking.bookId)
                                        Toast.makeText(context, "Đã gửi yêu cầu hủy", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                is TripState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lỗi: ${state.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                            Button(onClick = { /* Refresh logic */ }) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingTripCard(booking: Booking, onClick: () -> Unit, onCancel: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateRange = "${sdf.format(Date(booking.startDate))} - ${sdf.format(Date(booking.endDate))}"

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                if (booking.proImage.isNotEmpty()) {
                    AsyncImage(
                        model = booking.proImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                }
                
                val statusColor = when (booking.status) {
                    "confirmed" -> Color(0xFF4CAF50)
                    "pending" -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    val statusText = when(booking.status) {
                        "confirmed" -> "ĐÃ XÁC NHẬN"
                        "pending" -> "CHỜ XỬ LÝ"
                        "rejected" -> "BỊ TỪ CHỐI"
                        else -> "ĐÃ HỦY"
                    }
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(booking.proName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(dateRange, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tổng: đ${String.format(Locale.getDefault(), "%,.0f", booking.totalPrice)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    
                    if (booking.status == "pending") {
                        TextButton(onClick = onCancel) {
                            Text("Hủy đặt", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(onClick = onClick) {
                            Text("Xem lại >", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTripState(tabName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Không có chuyến đi $tabName", color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}
