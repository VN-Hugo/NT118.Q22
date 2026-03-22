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
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Biến lưu trữ input điểm đến
    var destination by remember { mutableStateOf("") }
    // Biến lưu kết quả AI
    var aiResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Khởi tạo Model (Sửa API Key của bạn vào đây)
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = "AIzaSyBjc3sAFpb-_gOPo8KqjOLrIhaZh4c_5Dg"
        )
    }

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
            value = destination, // Gán biến destination vào đây
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth(),
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

        val interests = listOf("Khám phá", "Ẩm thực", "Nghỉ dưỡng", "Văn hóa", "Mua sắm")
        val selectedInterests = remember { mutableStateListOf<String>() }

        SimpleFlowRow(spacing = 8.dp) {
            interests.forEach { interest ->
                val isSelected = selectedInterests.contains(interest)
                InterestTag(
                    text = interest,
                    icon = Icons.Default.Check, // Hoặc icon phù hợp
                    isSelected = isSelected,
                    onSelect = {
                        // Nếu đã chọn rồi thì bỏ chọn, chưa chọn thì thêm vào danh sách
                        if (isSelected) {
                            selectedInterests.remove(interest)
                        } else {
                            selectedInterests.add(interest)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (destination.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập điểm đến!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                coroutineScope.launch {
                    try {
                        // TỰ ĐỘNG TẠO CÂU LỆNH (PROMPT) TỪ GIAO DIỆN
                        val budgetText = if (budgetValue < 0.3f) "tiết kiệm" else if (budgetValue < 0.7f) "trung bình" else "sang trọng"
                        val prompt = "Hãy lên lịch trình du lịch đi $destination trong $duration ngày. " +
                                    "Ngân sách: $budgetText. " +
                                    "Sở thích: ${selectedInterests.joinToString(", ")}. " +
                                    "Trình bày rõ ràng từng ngày."
                        Log.d("AI_DEBUG", "Câu lệnh gửi đi: $prompt")

                        val response = generativeModel.generateContent(prompt)

                        Log.d("AI_DEBUG", "Kết quả trả về: ${response.text}")

                        aiResult = response.text ?: "Không có kết quả"
                        showResultDialog = true // Mở bảng kết quả
                    } catch (e: Exception) {
                        aiResult = "Lỗi: ${e.localizedMessage}"
                        showResultDialog = true
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
        ) {
//            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
//            Spacer(modifier = Modifier.width(10.dp))
//            Text("Tạo lịch trình bằng AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(20.dp)) // Dùng icon AutoAwesome cho "AI"
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tạo lịch trình bằng AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("Đóng") }
            },
            title = { Text("Lịch trình đề xuất ✨") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = aiResult)
                }
            }
        )
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
fun InterestTag(
    text: String,
    icon: ImageVector,
    isSelected: Boolean = false,
    onSelect: () -> Unit // Thêm dòng này để nhận sự kiện click
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF007BFF) else Color(0xFFF1F3F5),
        // SỬA Ở ĐÂY: Gọi onSelect() khi người dùng bấm vào
        modifier = Modifier.clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) Color.White else Color.DarkGray
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIPlannerPreview() {
    AIPlannerScreen()
}