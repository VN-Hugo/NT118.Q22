package com.example.travelapp.ui.owner

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BrandTealColor = Color(0xFF005D67)
private val SoftGrayColor = Color(0xFFF2F4F5)
private val TipGreenColor = Color(0xFFE0F2F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHotelScreen(
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: AddHotelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

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
            // Section 1: Basic Info
            SectionHeaderOwner("THÔNG TIN CƠ BẢN")
            OwnerInputField(
                label = "Tên khách sạn",
                value = viewModel.hotelName,
                onValueChange = { viewModel.hotelName = it },
                placeholder = "Nhập tên khách sạn..."
            )
            OwnerInputField(
                label = "Địa chỉ chi tiết",
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                placeholder = "Số nhà, tên đường, phường/xã..."
            )
            OwnerInputField(
                label = "Thành phố / Điểm đến",
                value = viewModel.desName,
                onValueChange = { viewModel.desName = it },
                placeholder = "Ví dụ: Đà Lạt"
            )

            // Section 2: Pricing & Description
            SectionHeaderOwner("GIÁ CẢ & MÔ TẢ")
            OwnerInputField(
                label = "Giá khởi điểm (đ/đêm)",
                value = viewModel.price,
                onValueChange = { viewModel.price = it },
                placeholder = "Ví dụ: 1200000"
            )
            OwnerInputField(
                label = "Mô tả khách sạn",
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                placeholder = "Giới thiệu về khách sạn của bạn...",
                isMultiLine = true
            )

            // Section 3: Amenities (Tags)
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
fun OwnerInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isMultiLine: Boolean = false
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandTealColor,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
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
