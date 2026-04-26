package com.example.travelapp.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.travelapp.data.model.Property
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.location.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onPropertyClick: (String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    // 1. Biến lưu tọa độ người dùng (null nếu chưa có quyền)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 2. Trình kích hoạt hộp thoại xin quyền
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Nếu User cho phép, gọi hàm lấy vị trí
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) { e.printStackTrace() }
        }
    }

    // 3. Tự động kiểm tra và xin quyền khi màn hình này được mở lên
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val exploreState by viewModel.exploreState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    // 1. THÊM BIẾN TRẠNG THÁI NÀY ĐỂ NHỚ LÀ ĐANG XEM MAP HAY LIST
    var isMapView by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Menu, contentDescription = null)
            Text("Smart Travel AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }

        // --- Search Bar ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Tìm kiếm điểm đến...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF1F3F4),
                focusedContainerColor = Color(0xFFF1F3F4),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // --- 2. SỬA LẠI KHU VỰC FILTER ĐỂ NHÉT THÊM NÚT CÔNG TẮC MAP/LIST ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cụm Filter Chips (Cho cuộn ngang nếu nhiều)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipItem("Tất cả", selectedType == null) { viewModel.onTypeChange(null) }
                FilterChipItem("Khách sạn", selectedType == "hotel") { viewModel.onTypeChange("hotel") }
                FilterChipItem("Hoạt động", selectedType == "activity") { viewModel.onTypeChange("activity") }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nút công tắc Map/List
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isMapView) "Bản đồ" else "Danh sách",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Switch(
                    checked = isMapView,
                    onCheckedChange = { isMapView = it },
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // --- Content ---
        when (val state = exploreState) {
            is ExploreState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            }
            is ExploreState.Success -> {
                if (state.properties.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Không có kết quả nào phù hợp.")
                    }
                } else {
                    // 3. XỬ LÝ GIAO DIỆN DỰA VÀO CÔNG TẮC
                    if (isMapView) {
                        // NẾU BẬT BẢN ĐỒ: Gọi hàm ExploreMapScreen bạn vừa tạo
                        ExploreMapScreen(
                            properties = state.properties,
                            onNavigateToDetail = { propertyId ->
                                onPropertyClick(propertyId)
                            }
                        )
                    } else {
                        // NẾU TẮT BẢN ĐỒ: Hiển thị dạng Grid View cũ của bạn
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.properties, key = { it.proId }) { property ->
                                val isFavorite = state.favoriteIds.contains(property.proId)
                                PropertyCard(
                                    property = property,
                                    isFavorite = isFavorite,
                                    userLocation = userLocation,
                                    onClick = { onPropertyClick(property.proId) },
                                    onToggleFavorite = { viewModel.toggleFavorite(property.proId) }
                                )
                            }
                        }
                    }
                }
            }
            is ExploreState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ${state.message}", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF1F3F4),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF2196F3)) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, color = if (isSelected) Color(0xFF2196F3) else Color.Black, fontSize = 14.sp)
        }
    }
}

fun calculateDistance(userLat: Double, userLng: Double, propertyLat: Double, propertyLng: Double): String {
    val results = FloatArray(1)
    Location.distanceBetween(userLat, userLng, propertyLat, propertyLng, results)

    val distanceInKm = results[0] / 1000.0 // Đổi từ mét sang Kilomet

    // Nếu khoảng cách dưới 1km thì hiện "Cách 800 m", nếu trên 1km thì hiện "Cách 2.5 km"
    return if (distanceInKm < 1.0) {
        "${(distanceInKm * 1000).toInt()} m"
    } else {
        String.format(java.util.Locale.getDefault(), "%.1f km", distanceInKm)
    }
}

@Composable
fun PropertyCard(
    property: Property,
    isFavorite: Boolean,
    userLocation: LatLng?,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()) {
                val imageUrl = property.images.firstOrNull { it.isPrimary }?.url ?: property.images.firstOrNull()?.url

                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                }

                // Nút Favorite
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = if (isFavorite) Color.White else Color.Black.copy(alpha = 0.3f),
                    onClick = { onToggleFavorite() } // Chuyển clickable vào Surface của Material3
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(property.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                        Text("${property.averageRating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                    }
                }
                Text(property.desName, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // Fix lỗi định dạng chuỗi ở đây
                val displayPrice = if (property.price > 0) {
                    "đ${String.format(Locale.getDefault(), "%,.0f", property.price.toDouble())}"
                } else {
                    "Chưa cập nhật"
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = displayPrice, fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 14.sp)
                    Text(text = if(property.type == "hotel") "/đêm" else "/vé", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(start = 2.dp, bottom = 1.dp))
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            // ... code Tên khách sạn và sao đánh giá ...

            Text(property.desName, color = Color.Gray, fontSize = 12.sp)

            // === ĐÂY LÀ PHẦN THÊM VÀO ===
            if (userLocation != null) {
                val distanceStr = calculateDistance(
                    userLat = userLocation.latitude,
                    userLng = userLocation.longitude,
                    propertyLat = property.latitude,
                    propertyLng = property.longitude
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(12.dp))
                    Text(" Cách bạn $distanceStr", color = Color(0xFF1976D2), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            // ==============================

            Spacer(modifier = Modifier.height(4.dp))
            // ... code giá tiền ...
        }
    }
}