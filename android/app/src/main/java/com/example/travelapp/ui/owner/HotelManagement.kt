package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextFieldDefaults

// Định nghĩa màu sắc thương hiệu
val BrandTeal = Color(0xFF005D67)
val SoftGray = Color(0xFFF2F4F5)
val TipGreen = Color(0xFFE0F2F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHotelScreen() {
    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = { Text("Thêm khách sạn mới", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Lưu thông tin", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = { DashboardBottomNav() } // Dùng lại từ code trước, nhớ set selected cho mục "Khách sạn"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Thông tin cơ bản
            SectionContainer(title = "Thông tin cơ bản", icon = Icons.Default.Info) {
                CustomInputField(label = "TÊN KHÁCH SẠN", placeholder = "Ví dụ: The Grand Editorial Resort")
                CustomInputField(label = "VỊ TRÍ (THÀNH PHỐ)", placeholder = "Đà Nẵng, Việt Nam", leadingIcon = Icons.Default.LocationOn)
                CustomInputField(label = "KHOẢNG GIÁ (ĐÊM)", placeholder = "2.500.000 - 5.000.000", trailingText = "VND")
                CustomInputField(label = "MÔ TẢ CHI TIẾT", placeholder = "Chia sẻ câu chuyện về khách sạn của bạn...", isMultiLine = true)
            }

            // 2. Tiện ích dịch vụ
            SectionContainer(title = "Tiện ích dịch vụ", icon = Icons.Default.Phone, subtitle = "Chọn các tiện ích nổi bật của khách sạn") {
                val amenities = listOf(
                    "Wifi miễn phí" to Icons.Default.Place,
                    "Hồ bơi" to Icons.Default.Person,
                    "Bãi đậu xe" to Icons.Default.Search,
                    "Nhà hàng" to Icons.Default.LocationOn,
                    "Spa & Massage" to Icons.Default.Lock,
                    "Phòng Gym" to Icons.Default.LocationOn,
                    "Máy lạnh" to Icons.Default.Close,
                    "Dịch vụ phòng" to Icons.Default.Lock
                )

                // Grid 2 cột cho tiện ích
                Column {
                    for (i in amenities.indices step 2) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AmenitySelectableCard(amenities[i].first, amenities[i].second, modifier = Modifier.weight(1f))
                            if (i + 1 < amenities.size) {
                                AmenitySelectableCard(amenities[i+1].first, amenities[i+1].second, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // 3. Thư viện ảnh
            SectionContainer(title = "Thư viện ảnh", icon = Icons.Default.Person, trailingText = "3 / 10") {
                PhotoGalleryGrid()
                Spacer(modifier = Modifier.height(12.dp))
                AIConsultantBox()
            }

            // 4. Trạng thái vận hành
            SectionContainer(title = "Trạng thái vận hành", icon = null) {
                StatusToggleRow("Hiển thị công khai", true)
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = SoftGray)
                StatusVerificationRow()
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionContainer(title: String, icon: ImageVector?, subtitle: String? = null, trailingText: String? = null, content: @Composable () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Box(modifier = Modifier.size(32.dp).background(TipGreen, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = BrandTeal, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                if (trailingText != null) {
                    Text(trailingText, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun CustomInputField(label: String, placeholder: String, leadingIcon: ImageVector? = null, trailingText: String? = null, isMultiLine: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().heightIn(min = if (isMultiLine) 100.dp else 50.dp),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) } },
            trailingIcon = trailingText?.let { { Text(it, modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp) } },
            colors = TextFieldDefaults.colors( // Dùng .colors thay vì .outlinedTextFieldColors
                focusedContainerColor = SoftGray,
                unfocusedContainerColor = SoftGray,
                disabledContainerColor = SoftGray,
                focusedIndicatorColor = BrandTeal,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun AmenitySelectableCard(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    var isSelected by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.height(60.dp).clickable { isSelected = !isSelected },
        color = if (isSelected) Color.White else SoftGray,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) BrandTeal else Color.Transparent)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = if (isSelected) BrandTeal else Color.DarkGray, modifier = Modifier.size(20.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PhotoGalleryGrid() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Mockup 3 ảnh đầu
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)) {
            Text("ẢNH BÌA", modifier = Modifier.align(Alignment.BottomStart).padding(4.dp).background(Color.Black.copy(0.6f)).padding(2.dp), color = Color.White, fontSize = 8.sp)
            IconButton(onClick = {}, modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.White, CircleShape)) {
                Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(12.dp))
            }
        }
        // Thêm ảnh button
        Box(
            modifier = Modifier.size(100.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)).background(SoftGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, null, tint = BrandTeal)
                Text("THÊM ẢNH", fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AIConsultantBox() {
    Surface(color = TipGreen, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(Icons.Default.Add, null, tint = BrandTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Mẹo từ AI Concierge", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTeal)
                Text("Khách sạn có trên 5 ảnh chất lượng cao thường nhận được lượt đặt phòng cao hơn 35%...", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun StatusToggleRow(label: String, initialValue: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = BrandTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Medium)
        }
        Switch(checked = initialValue, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandTeal))
    }
}

@Composable
fun StatusVerificationRow() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = BrandTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Xác thực bởi Admin", fontWeight = FontWeight.Medium)
            }
        }
        Surface(color = TipGreen, shape = RoundedCornerShape(4.dp)) {
            Text("CHỜ DUYỆT", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandTeal)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddHotelPreview() {
    AddHotelScreen()
}