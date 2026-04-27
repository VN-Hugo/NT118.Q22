package com.example.travelapp.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    onBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val bookingUiState by viewModel.bookingUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookingUiState) {
        when (bookingUiState) {
            is BookingUiState.Success -> {
                Toast.makeText(context, "Đặt phòng thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetBookingState()
                onBack()
            }
            is BookingUiState.Error -> {
                Toast.makeText(context, (bookingUiState as BookingUiState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetBookingState()
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = {
            if (state is PropertyDetailState.Success) {
                BookingBottomBar(
                    totalPrice = viewModel.totalBookingPrice,
                    isLoading = bookingUiState is BookingUiState.Loading,
                    onBookingConfirm = { viewModel.createBooking() }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is PropertyDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PropertyDetailState.Success -> {
                    PropertyDetailContent(
                        property = currentState.property,
                        roomTypes = currentState.roomTypes,
                        viewModel = viewModel,
                        onBack = onBack
                    )
                }
                is PropertyDetailState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailContent(
    property: Property,
    roomTypes: List<RoomType>,
    viewModel: PropertyDetailViewModel,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDatesSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    showDatePicker = false
                }) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // --- Image Header ---
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            val primaryImage = property.images.firstOrNull { it.isPrimary }?.url ?: property.images.firstOrNull()?.url
            AsyncImage(
                model = primaryImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // --- Title & Rating ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(property.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(property.address, color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Text(property.averageRating.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Date Selection ---
            Text("Thời gian lưu trú", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { showDatePicker = true },
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.width(12.dp))
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val startText = viewModel.startDate?.let { sdf.format(Date(it)) } ?: "Chọn ngày đến"
                    val endText = viewModel.endDate?.let { sdf.format(Date(it)) } ?: "Chọn ngày đi"
                    Text("$startText - $endText", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Room Types Section ---
            if (roomTypes.isNotEmpty()) {
                Text("Chọn hạng phòng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                roomTypes.forEach { room ->
                    RoomTypeItem(
                        room = room,
                        isSelected = viewModel.selectedRoomType?.roomTypeId == room.roomTypeId,
                        onSelect = { viewModel.onRoomTypeSelected(room) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Description ---
            Text("Mô tả", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                property.description.ifEmpty { "Chưa có mô tả chi tiết." },
                color = Color.Gray,
                lineHeight = 22.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // CODE GỌI BẢN ĐỒ
            Spacer(modifier = Modifier.height(16.dp))
            PropertyMiniMap(
                latitude = property.latitude,
                longitude = property.longitude,
                propertyName = property.name
            )
        }
    }
}

@Composable
fun RoomTypeItem(room: RoomType, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF8F9FA)),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(room.typeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(room.amenities.joinToString(" • "), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "đ${String.format(Locale.getDefault(), "%,.0f", room.price)} / đêm",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            RadioButton(selected = isSelected, onClick = onSelect)
        }
    }
}

@Composable
fun BookingBottomBar(
    totalPrice: Double,
    isLoading: Boolean,
    onBookingConfirm: () -> Unit
) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tổng thanh toán", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = "đ${String.format(Locale.getDefault(), "%,.0f", totalPrice)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            Button(
                onClick = onBookingConfirm,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Xác nhận đặt", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}


@Composable
fun PropertyMiniMap(
    latitude: Double,
    longitude: Double,
    propertyName: String
) {
    val context = LocalContext.current
    val location = LatLng(latitude, longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f) // Zoom level 15 là nhìn rõ phố phường
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = "Vị trí trên bản đồ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Khung Map
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                scrollGesturesEnabled = false // Khóa cuộn để vuốt màn hình không bị vướng
            )
        ) {
            Marker(
                state = MarkerState(position = location),
                title = propertyName
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nút bấm chuyển hướng sang app Google Maps
        OutlinedButton(
            onClick = {
                val uri = Uri.parse("google.navigation:q=$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Directions, contentDescription = "Chỉ đường")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chỉ đường đến đây")
        }
    }
}
