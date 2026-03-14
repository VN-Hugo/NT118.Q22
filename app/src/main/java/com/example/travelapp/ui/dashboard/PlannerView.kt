package com.example.travelapp.ui.planner

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen() {
    var duration by remember { mutableIntStateOf(3) }
    var budgetValue by remember { mutableFloatStateOf(0.5f)  }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.weight(1f))
            Text("Trợ lý Du lịch AI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(24.dp))
        }

        Text(text = "Lên kế hoạch cho chuyến đi", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
        Text(text = "Để AI thiết kế lịch trình hoàn hảo cho bạn.", fontSize = 15.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(32.dp))

        SectionLabel("ĐIỂM ĐẾN")
        OutlinedTextField(
            value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Bạn muốn đi đâu?", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("THỜI GIAN (NGÀY)")
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF8FAFC)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { if (duration > 1) duration-- }, modifier = Modifier.background(Color.White, CircleShape).size(44.dp)) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF2196F3))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$duration", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("NGÀY", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { duration++ }, modifier = Modifier.background(Color.White, CircleShape).size(44.dp)) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF2196F3))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("MỨC NGÂN SÁCH")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tiết kiệm", color = Color.Gray, fontSize = 14.sp)
                Text("Sang chảnh", color = Color(0xFF2196F3), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = budgetValue,
                onValueChange = { budgetValue = it },
                modifier = Modifier.fillMaxWidth(),
                thumb = {
                    // Tùy chỉnh cái nút kéo tròn trịa có viền trắng
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = Color(0xFF2196F3),
                        border = BorderStroke(4.dp, Color.White),
                        shadowElevation = 4.dp
                    ) {}
                },
                track = { sliderState ->
                    // Tùy chỉnh thanh ray (đoạn đã đi qua màu xanh, đoạn chưa tới màu xám nhạt)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp) // Độ dày thanh kéo
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)) // Màu nền xám nhạt
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(budgetValue) // Chỉ dài đến vị trí nút kéo
                                .fillMaxHeight()
                                .background(Color(0xFF2196F3)) // Màu xanh active
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("SỞ THÍCH DU LỊCH")
        // SỬA LỖI Ở ĐÂY: Dùng Layout thủ công để tạo FlowRow "bất tử"
        SimpleFlowRow(spacing = 8.dp) {
            InterestTag("Khám phá", Icons.Default.Person, isSelected = true)
            InterestTag("Ẩm thực", Icons.Default.Phone)
            InterestTag("Nghỉ dưỡng", Icons.Default.Person)
            InterestTag("Văn hóa", Icons.Default.Person)
            InterestTag("Mua sắm", Icons.Default.Person)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
        ) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Tạo lịch trình bằng AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// Hàm Layout giúp các Tag tự nhảy xuống dòng mà không sợ lỗi thư viện
@Composable
fun SimpleFlowRow(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }

        var totalHeight = 0
        var currentRowWidth = 0
        var currentRowHeight = 0

        // Tính toán tổng chiều cao trước khi đặt vị trí
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
        totalHeight += currentRowHeight // Cộng dòng cuối cùng

        // Ép kiểu chiều cao về con số thực tế thay vì vô hạn
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

@Composable
fun SectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun InterestTag(text: String, icon: ImageVector, isSelected: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF007BFF) else Color(0xFFF1F3F5),
        modifier = Modifier.clickable { }
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) Color.White else Color.DarkGray)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = if (isSelected) Color.White else Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIPlannerPreview() {
    AIPlannerScreen()
}