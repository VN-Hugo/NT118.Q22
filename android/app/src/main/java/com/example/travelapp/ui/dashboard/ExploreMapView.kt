package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.travelapp.data.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreMapScreen(
    properties: List<Property>, // Truyền danh sách các phòng vào đây
    onNavigateToDetail: (String) -> Unit // Hàm chuyển hướng sang trang chi tiết
) {
    // Biến lưu trữ khách sạn đang được bấm chọn
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    // Khởi tạo camera ở vị trí mặc định (Ví dụ: Trung tâm map hoặc vị trí phòng đầu tiên)
    val initialLocation = properties.firstOrNull()?.let {
        LatLng(it.latitude, it.longitude)
    } ?: LatLng(10.762622, 106.660172) // Mặc định TP.HCM nếu list rỗng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. LỚP BẢN ĐỒ FULL MÀN HÌNH
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            properties.forEach { property ->
                // Thay vì Marker thường, dùng MarkerComposable để tự vẽ UI (Cục giá tiền)
                MarkerComposable(
                    state = MarkerState(position = LatLng(property.latitude, property.longitude)),
                    onClick = {
                        selectedProperty = property // Lưu lại phòng được click
                        true // Trả về true để camera không tự động focus giật cục
                    }
                ) {
                    // Vẽ cục nhãn giá tiền giống Airbnb
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedProperty?.proId == property.proId) Color.Black else Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "$${property.price}", // Giả sử model có biến price
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            color = if (selectedProperty?.proId == property.proId) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        // 2. BOTTOM SHEET TRƯỢT LÊN KHI BẤM VÀO MARKER
        if (selectedProperty != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedProperty = null }, // Đóng khi bấm ra ngoài
            ) {
                PropertyMiniCard(
                    property = selectedProperty!!,
                    onClick = {
                        selectedProperty?.proId?.let { id -> onNavigateToDetail(id) }
                    }
                )
            }
        }
    }
}

// Giao diện cái Card nhỏ hiện trong Bottom Sheet
@Composable
fun PropertyMiniCard(property: Property, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 24.dp) // Tránh nút home ảo
            .clickable { onClick() }
    ) {
        // Ảnh bìa
        AsyncImage(
            model = property.images.firstOrNull()?.url ?: "https://via.placeholder.com/400",
            contentDescription = "Property Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Thông tin
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = property.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = property.address, color = Color.Gray, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                Text(text = property.averageRating.toString(), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$${property.price} / đêm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}