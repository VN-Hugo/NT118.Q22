package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelapp.domain.model.RoomType
import java.util.Locale

private val BrandTealColor = Color(0xFF005D67)
private val SoftTealBgColor = Color(0xFFE0F2F1)
private val StatusGreenColor = Color(0xFF28A745)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    onBack: () -> Unit,
    onAddRoomClick: () -> Unit,
    viewModel: RoomManagementViewModel = hiltViewModel()
) {
    val roomState by viewModel.roomState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = { Text("Quản lý hạng phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = roomState) {
                is RoomManagementState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = BrandTealColor) }
                }
                is RoomManagementState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("QUẢN LÝ HỆ THỐNG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Hạng phòng & Giá", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BrandTealColor)
                            Spacer(Modifier.height(16.dp))
                            
                            Button(
                                onClick = onAddRoomClick,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thêm hạng phòng mới", fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(24.dp))
                        }

                        if (state.rooms.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                                    Text("Chưa có hạng phòng nào. Hãy thêm phòng đầu tiên!", color = Color.Gray)
                                }
                            }
                        } else {
                            items(state.rooms) { room ->
                                RoomTypeCard(room)
                            }
                        }
                        
                        item { Spacer(Modifier.height(40.dp)) }
                    }
                }
                is RoomManagementState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message, color = Color.Red) }
                }
            }
        }
    }
}

@Composable
fun RoomTypeCard(room: RoomType) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(room.typeName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(room.amenities.joinToString(" • "), fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("GIÁ/ĐÊM", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("đ${String.format(Locale.getDefault(), "%,.0f", room.price)}", fontWeight = FontWeight.Bold, color = BrandTealColor, fontSize = 16.sp)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F1F1))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(" Tổng số: ${room.totalRooms} phòng", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                }
                Row {
                    IconButton(onClick = {}) { Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp)) }
                }
            }
        }
    }
}
