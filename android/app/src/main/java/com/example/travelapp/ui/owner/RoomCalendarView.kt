package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

private val BrandTealColor = Color(0xFF005D67)
private val FullRedColor = Color(0xFFD32F2F)
private val AvailableGreenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCalendarScreen(
    onBack: () -> Unit,
    viewModel: RoomCalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedHotelId by viewModel.selectedHotelId.collectAsState()
    val selectedRoomId by viewModel.selectedRoomTypeId.collectAsState()

    var showHotelPicker by remember { mutableStateOf(false) }
    var showRoomPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch trạng thái phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            // --- Selection Header ---
            Surface(shadowElevation = 2.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = true,
                            onClick = { showHotelPicker = true },
                            label = { Text(if (selectedHotelId == null) "Chọn khách sạn" else "Khách sạn") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = true,
                            onClick = { showRoomPicker = true },
                            label = { Text(if (selectedRoomId == null) "Chọn hạng phòng" else "Hạng phòng") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- Month Selector ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                }
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale("vi", "VN")).format(currentMonth.time),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTealColor
                )
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }

            // --- Calendar Grid ---
            when (val state = uiState) {
                is CalendarUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = BrandTealColor) }
                }
                is CalendarUiState.Success -> {
                    // Logic to build calendar days
                    val days = remember(currentMonth, state.occupancyMap) {
                        getDaysOfMonth(currentMonth)
                    }
                    val selectedRoom = state.roomTypes.find { it.roomTypeId == selectedRoomId }
                    val totalRooms = selectedRoom?.totalRooms ?: 1

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.padding(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        // Weekday headers
                        val weekdays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        items(weekdays) {
                            Box(Modifier.padding(8.dp), Alignment.Center) {
                                Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }

                        items(days) { date ->
                            if (date == null) {
                                Spacer(Modifier.size(40.dp))
                            } else {
                                val occupied = state.occupancyMap[date.timeInMillis] ?: 0
                                val available = (totalRooms - occupied).coerceAtLeast(0)
                                val isPast = date.timeInMillis < System.currentTimeMillis() - 86400000

                                DayItem(
                                    day = date.get(Calendar.DAY_OF_MONTH).toString(),
                                    available = available,
                                    isFull = available == 0,
                                    isPast = isPast
                                )
                            }
                        }
                    }
                    
                    // Dialogs
                    HotelFilterDialog(showHotelPicker, state.hotels, { showHotelPicker = false }, { viewModel.onHotelSelected(it!!) })
                    RoomFilterDialog(showRoomPicker, state.roomTypes, { showRoomPicker = false }, { viewModel.onRoomTypeSelected(it!!) })
                }
                is CalendarUiState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message, color = Color.Red) }
                }
            }
            
            // --- Legend ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(AvailableGreenColor, "Còn phòng")
                Spacer(Modifier.width(16.dp))
                LegendItem(FullRedColor, "Hết phòng")
            }
        }
    }
}

@Composable
fun DayItem(day: String, available: Int, isFull: Boolean, isPast: Boolean) {
    Card(
        modifier = Modifier.padding(4.dp).aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPast -> Color(0xFFF1F1F1)
                isFull -> FullRedColor.copy(alpha = 0.1f)
                else -> AvailableGreenColor.copy(alpha = 0.1f)
            }
        ),
        border = if (!isPast) BorderStroke(1.dp, if (isFull) FullRedColor else AvailableGreenColor) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(day, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isPast) Color.Gray else Color.Black)
            if (!isPast) {
                Text(
                    text = if (isFull) "FULL" else "Còn $available",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isFull) FullRedColor else AvailableGreenColor
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color, CircleShape))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

fun getDaysOfMonth(month: Calendar): List<Calendar?> {
    val cal = month.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val days = mutableListOf<Calendar?>()
    for (i in 0 until firstDayOfWeek) days.add(null)
    for (i in 1..daysInMonth) {
        val day = cal.clone() as Calendar
        day.set(Calendar.DAY_OF_MONTH, i)
        // Reset time
        day.set(Calendar.HOUR_OF_DAY, 0); day.set(Calendar.MINUTE, 0); day.set(Calendar.SECOND, 0); day.set(Calendar.MILLISECOND, 0)
        days.add(day)
    }
    return days
}
