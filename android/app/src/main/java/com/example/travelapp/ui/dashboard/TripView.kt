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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Plan
import com.example.travelapp.data.model.PlanDay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    onBookingClick: (String) -> Unit,
    onPropertyClick: (String) -> Unit = {},
    viewModel: TripViewModel = hiltViewModel()
) {
    val tripState by viewModel.tripState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sắp tới", "Đã lưu", "Đã đi", "Kế hoạch")
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
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                divider = {},
                edgePadding = 0.dp
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
                    when (selectedTabIndex) {
                        0, 1, 2 -> {
                            // Booking tabs (existing logic)
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
                        3 -> {
                            // Plans tab (NEW)
                            if (state.plans.isEmpty()) {
                                EmptyPlanState()
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.plans, key = { it.planId }) { plan ->
                                        SavedPlanCard(
                                            plan = plan,
                                            onPropertyClick = onPropertyClick,
                                            onDelete = {
                                                viewModel.deletePlan(plan.planId)
                                                Toast.makeText(context, "Đã xóa kế hoạch", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
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

// ========================
// SAVED PLAN CARD
// ========================

@Composable
fun SavedPlanCard(
    plan: Plan,
    onPropertyClick: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val createdDate = sdf.format(Date(plan.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Header - always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plan icon
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Map, null, tint = Color(0xFF1565C0), modifier = Modifier.size(26.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "📍 ${plan.destination}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${plan.duration} ngày · ${plan.budget}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Tạo: $createdDate",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                // Expand/collapse icon
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            // Expanded content - day-by-day itinerary
            if (expanded) {
                HorizontalDivider(color = Color(0xFFF1F5F9))

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    plan.days.forEach { day ->
                        PlanDaySummary(
                            planDay = day,
                            onHotelClick = { propertyId ->
                                if (propertyId.isNotEmpty()) {
                                    onPropertyClick(propertyId)
                                }
                            }
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Delete button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xóa kế hoạch", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ========================
// PLAN DAY SUMMARY (for saved plans)
// ========================

@Composable
private fun PlanDaySummary(
    planDay: PlanDay,
    onHotelClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            planDay.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(4.dp))

        planDay.activities.forEach { activity ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                if (activity.time.isNotBlank()) {
                    Text(
                        activity.time,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.width(48.dp)
                    )
                }
                Text(activity.title, fontSize = 13.sp, color = Color(0xFF424242))
            }
        }

        // Hotel recommendation
        planDay.suggestedHotel?.let { hotel ->
            if (hotel.name.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .then(
                            if (hotel.propertyId.isNotEmpty())
                                Modifier.clickable { onHotelClick(hotel.propertyId) }
                            else Modifier
                        ),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF3E5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏨", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            hotel.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7B1FA2),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (hotel.pricePerNight > 0) {
                            Text(
                                "đ${String.format("%,.0f", hotel.pricePerNight)}",
                                fontSize = 11.sp,
                                color = Color(0xFF7B1FA2)
                            )
                        }
                        if (hotel.propertyId.isNotEmpty()) {
                            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF7B1FA2), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color(0xFFF5F5F5))
    }
}

// ========================
// BOOKING CARD (preserved from original)
// ========================

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

// ========================
// EMPTY STATES
// ========================

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

@Composable
fun EmptyPlanState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Map, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Chưa có kế hoạch nào", color = Color.Gray, fontWeight = FontWeight.Medium)
        Text("Hãy dùng AI Planner để tạo kế hoạch du lịch!", fontSize = 13.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
    }
}
