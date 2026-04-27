package com.example.travelapp.ui.owner

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun LocationPickerScreen(
    onLocationSelected: (LatLng, String) -> Unit, // Trả về tọa độ và địa chỉ chữ
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Tọa độ mặc định (ví dụ trung tâm TP.HCM)
    val defaultLocation = LatLng(10.762622, 106.660172)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    var addressText by remember { mutableStateOf("Đang lấy địa chỉ...") }
    var isMoving by remember { mutableStateOf(false) }

    // Mỗi khi camera dừng di chuyển, ta lấy tọa độ ở tâm để dịch ra địa chỉ
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            isMoving = false
            val center = cameraPositionState.position.target
            addressText = getAddressFromLocation(context, center.latitude, center.longitude)
        } else {
            isMoving = true
            addressText = "Đang di chuyển..."
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Bản đồ nền
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )

        // 2. Cái ghim cố định ở chính giữa màn hình
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
                .padding(bottom = 24.dp) // Căn chỉnh để đuôi ghim chỉ đúng tâm
        )

        // 3. Thanh hiển thị địa chỉ và nút xác nhận ở dưới cùng
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Vị trí nhà của bạn:", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = addressText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalLocation = cameraPositionState.position.target
                        onLocationSelected(finalLocation, addressText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isMoving && addressText != "Không tìm thấy địa chỉ"
                ) {
                    Text("Xác nhận vị trí này")
                }
            }
        }
    }
}

// Hàm dùng Geocoder để dịch Tọa độ -> Địa chỉ chữ
private suspend fun getAddressFromLocation(
    context: android.content.Context,
    lat: Double,
    lng: Double
): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val geocoder = Geocoder(context, Locale("vi", "VN"))
        val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0) // Lấy dòng địa chỉ đầy đủ
        } else {
            "Không tìm thấy địa chỉ"
        }
    } catch (e: Exception) {
        "Lỗi lấy địa chỉ"
    }
}