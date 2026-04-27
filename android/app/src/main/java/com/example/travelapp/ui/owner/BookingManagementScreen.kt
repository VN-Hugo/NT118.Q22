package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelapp.R
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import java.text.SimpleDateFormat
import java.util.*

private val BrandTealColor = Color(0xFF005D67)
private val BgGrayColor = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingManagementScreen(
    onNavigateToCalendar: () -> Unit,
    viewModel: BookingManagementViewModel = hiltViewModel()
) {
    val state by viewModel.bookingState.collectAsState()
    val filteredBookings by viewModel.filteredBookings.collectAsState()
    val selectedHotelId by viewModel.selectedHotelId.collectAsState()
    val selectedRoomId by viewModel.selectedRoomTypeId.collectAsState()

    var showHotelFilter by remember { mutableStateOf(false) }
    var showRoomFilter by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGrayColor)
    ) {
        TopAppBar(
            title = { Text("Quản lý đơn đặt", fontWeight = FontWeight.Bold, color = BrandTealColor) },
            actions = {
                IconButton(onClick = onNavigateToCalendar) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history), // Bạn có thể đổi sang ic_calendar nếu có
                        contentDescription = "Lịch phòng",
                        tint = BrandTealColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // --- Filter Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedHotelId != null,
                onClick = { showHotelFilter = true },
                label = { Text(if (selectedHotelId == null) "Tất cả khách sạn" else "Đã chọn KS") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera), // Đổi sang ic_hotel nếu có
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
            FilterChip(
                selected = selectedRoomId != null,
                onClick = { showRoomFilter = true },
                label = { Text(if (selectedRoomId == null) "Tất cả phòng" else "Đã chọn phòng") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history), // Đổi sang ic_bed nếu có
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
            if (selectedHotelId != null || selectedRoomId != null) {
                IconButton(onClick = { viewModel.onHotelSelected(null) }) {
                    Icon(painterResource(id = R.drawable.ic_filter), contentDescription = "Bỏ lọc", tint = Color.Red)
                }
            }
        }

        when (val currentState = state) {
            is BookingManagementState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = BrandTealColor) }
            }
            is BookingManagementState.Success -> {
                HotelFilterDialog(
                    show = showHotelFilter,
                    hotels = currentState.hotels,
                    onDismiss = { showHotelFilter = false },
                    onSelect = { viewModel.onHotelSelected(it) }
                )

                RoomFilterDialog(
                    show = showRoomFilter,
                    rooms = if (selectedHotelId != null) currentState.roomTypesMap[selectedHotelId] ?: emptyList() else emptyList(),
                    onDismiss = { showRoomFilter = false },
                    onSelect = { viewModel.onRoomSelected(it) }
                )

                if (filteredBookings.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Không có đơn đặt phòng nào", color = Color.Gray) }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredBookings, key = { it.bookId }) { booking ->
                            BookingManagementCard(
                                booking = booking,
                                onAccept = { viewModel.updateStatus(booking.bookId, "confirmed") },
                                onReject = { viewModel.updateStatus(booking.bookId, "rejected") }
                            )
                        }
                    }
                }
            }
            is BookingManagementState.Error -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(currentState.message, color = Color.Red) }
            }
        }
    }
}

@Composable
fun HotelFilterDialog(show: Boolean, hotels: List<Property>, onDismiss: () -> Unit, onSelect: (String?) -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Chọn khách sạn") },
            text = {
                LazyColumn {
                    item {
                        TextButton(onClick = { onSelect(null); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Tất cả khách sạn", textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                    }
                    items(hotels) { hotel ->
                        TextButton(onClick = { onSelect(hotel.proId); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                            Text(hotel.name, textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
        )
    }
}

@Composable
fun RoomFilterDialog(show: Boolean, rooms: List<RoomType>, onDismiss: () -> Unit, onSelect: (String?) -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Chọn hạng phòng") },
            text = {
                if (rooms.isEmpty()) {
                    Text("Vui lòng chọn khách sạn trước", color = Color.Gray)
                } else {
                    LazyColumn {
                        item {
                            TextButton(onClick = { onSelect(null); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Tất cả hạng phòng", textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        items(rooms) { room ->
                            TextButton(onClick = { onSelect(room.roomTypeId); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                                Text(room.typeName, textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
        )
    }
}

@Composable
fun BookingManagementCard(booking: Booking, onAccept: () -> Unit, onReject: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateRange = "${sdf.format(Date(booking.startDate))} - ${sdf.format(Date(booking.endDate))}"

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).background(Color(0xFFECEFF1), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(painterResource(id = R.drawable.ic_camera), null, tint = BrandTealColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Đơn: #${booking.bookId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(booking.proName, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                }

                val statusColor = when(booking.status) {
                    "confirmed" -> Color(0xFF4CAF50)
                    "pending" -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = when(booking.status) {
                            "confirmed" -> "ĐÃ DUYỆT"
                            "pending" -> "CHỜ DUYỆT"
                            else -> "ĐÃ HỦY"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BgGrayColor)

            BookingInfoRow(painterResource(id = R.drawable.ic_history), dateRange)
            booking.hotelBooking?.let {
                BookingInfoRow(painterResource(id = R.drawable.ic_history), "Số lượng: ${it.quantity} phòng")
            }
            BookingInfoRow(painterResource(id = R.drawable.ic_history), "Tổng thu: đ${String.format(Locale.getDefault(), "%,.0f", booking.totalPrice)}", true)

            if (booking.status == "pending") {
                Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onAccept, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BrandTealColor), shape = RoundedCornerShape(12.dp)) {
                        Text("Duyệt đơn", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.Red)) {
                        Text("Từ chối", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingInfoRow(icon: Painter, text: String, isPrice: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, null, tint = if (isPrice) BrandTealColor else Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = if (isPrice) BrandTealColor else Color.DarkGray, fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal)
    }
}
