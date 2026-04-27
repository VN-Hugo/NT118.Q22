package com.example.travelapp.ui.owner

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.travelapp.R

private val BrandTealColor = Color(0xFF005D67)
private val SoftGrayColor = Color(0xFFF2F4F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHotelScreen(
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: AddHotelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var isCityMenuExpanded by remember { mutableStateOf(false) }

    // --- MỚI THÊM: Biến bật/tắt màn hình bản đồ ---
    var showMapPicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.onImagesSelected(uris)
    }

    LaunchedEffect(state) {
        when (state) {
            is AddHotelState.Success -> {
                Toast.makeText(context, "Đã lưu khách sạn thành công!", Toast.LENGTH_SHORT).show()
                onSuccess((state as AddHotelState.Success).proId)
            }
            is AddHotelState.Error -> {
                Toast.makeText(context, (state as AddHotelState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // --- HIỆN BẢN ĐỒ NẾU showMapPicker = true ---
    if (showMapPicker) {
        LocationPickerScreen(
            onLocationSelected = { latLng, addressStr ->
                // Cập nhật tọa độ và địa chỉ vào ViewModel
                viewModel.latitude = latLng.latitude
                viewModel.longitude = latLng.longitude
                viewModel.address = TextFieldValue(addressStr)
                showMapPicker = false // Tắt bản đồ
            },
            onBack = { showMapPicker = false }
        )
    } else {
        // --- NẾU KHÔNG BẬT BẢN ĐỒ THÌ HIỆN GIAO DIỆN NHẬP BÌNH THƯỜNG ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Đăng ký khách sạn", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        if (state is AddHotelState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandTealColor)
                        } else {
                            TextButton(onClick = { viewModel.saveHotel() }) {
                                Text("LƯU", color = BrandTealColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SectionHeaderOwner("THÔNG TIN CƠ BẢN")
                OwnerInputField(
                    label = "Tên khách sạn",
                    value = viewModel.hotelName,
                    onValueChange = { viewModel.hotelName = it },
                    placeholder = "Nhập tên khách sạn..."
                )

                // --- SỬA Ở ĐÂY: Thêm trailingIcon gọi bản đồ ---
                OwnerInputField(
                    label = "Địa chỉ chi tiết",
                    value = viewModel.address,
                    onValueChange = { viewModel.address = it },
                    placeholder = "Số nhà, tên đường, phường/xã...",
                    trailingIcon = {
                        IconButton(onClick = { showMapPicker = true }) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Chọn từ bản đồ",
                                tint = BrandTealColor
                            )
                        }
                    }
                )

                // Dropdown chọn thành phố
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Thành phố", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = isCityMenuExpanded,
                        onExpandedChange = { isCityMenuExpanded = !isCityMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.desName,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Chọn thành phố", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCityMenuExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandTealColor,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = isCityMenuExpanded,
                            onDismissRequest = { isCityMenuExpanded = false }
                        ) {
                            viewModel.provinceList.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        viewModel.desName = city
                                        isCityMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                SectionHeaderOwner("MÔ TẢ KHÁCH SẠN")
                OwnerInputField(
                    label = "Giới thiệu",
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    placeholder = "Giới thiệu về khách sạn của bạn...",
                    isMultiLine = true
                )

                SectionHeaderOwner("HÌNH ẢNH KHÁCH SẠN")
                if (viewModel.selectedImages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(SoftGrayColor, RoundedCornerShape(12.dp))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add_photo),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Text("Bấm để chọn ảnh từ thư viện", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.selectedImages.forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(SoftGrayColor, RoundedCornerShape(8.dp))
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = BrandTealColor)
                        }
                    }
                    TextButton(onClick = { viewModel.onImagesSelected(emptyList()) }) {
                        Text("Xóa tất cả ảnh", color = Color.Red, fontSize = 12.sp)
                    }
                }

                SectionHeaderOwner("TIỆN ÍCH NỔI BẬT")
                FlowRowOwner(spacing = 8.dp) {
                    val tags = listOf("Wifi", "Hồ bơi", "Spa", "Nhà hàng", "Bãi biển", "Phòng gym", "Buffet")
                    tags.forEach { tag ->
                        val isSelected = viewModel.selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onTagToggle(tag) },
                            label = { Text(tag) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandTealColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun OwnerInputField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isMultiLine: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null // <--- THÊM DÒNG NÀY
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = if (isMultiLine) 120.dp else 56.dp),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            trailingIcon = trailingIcon, // <--- THÊM DÒNG NÀY
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandTealColor,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun SectionHeaderOwner(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun FlowRowOwner(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
        var totalHeight = 0
        var currentRowWidth = 0
        var currentRowHeight = 0
        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width > constraints.maxWidth) {
                totalHeight += currentRowHeight + spacingPx
                currentRowWidth = placeable.width + spacingPx
                currentRowHeight = placeable.height
            } else {
                currentRowWidth += placeable.width + spacingPx
                currentRowHeight = maxOf(currentRowHeight, placeable.height)
            }
        }
        totalHeight += currentRowHeight
        layout(constraints.maxWidth, totalHeight) {
            var x = 0
            var y = 0
            var lineHeight = 0
            placeables.forEach { placeable ->
                if (x + placeable.width > constraints.maxWidth) {
                    x = 0
                    y += lineHeight + spacingPx
                    lineHeight = 0
                }
                placeable.placeRelative(x, y)
                x += placeable.width + spacingPx
                lineHeight = maxOf(lineHeight, placeable.height)
            }
        }
    }
}
