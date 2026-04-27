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
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. CÁC BIẾN LƯU TRỮ INPUT
    var destination by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(3) }

    // Đã chuyển Ngân sách và Sở thích thành dạng chuỗi (String) để người dùng tự nhập
    var budgetInput by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }

    // Biến quản lý trạng thái UI
    var aiResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Khởi tạo Model
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = com.example.travelapp.BuildConfig.GEMINI_API_KEY
        )
    }

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

        // --- ĐIỂM ĐẾN ---
        SectionLabel("ĐIỂM ĐẾN")
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Bạn muốn đi đâu?", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- THỜI GIAN ---
        SectionLabel("THỜI GIAN (NGÀY)")
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF8FAFC)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { if (duration > 1) duration-- }, modifier = Modifier.background(Color.White, CircleShape).size(44.dp)) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF2196F3)) // Có thể đổi thành Remove icon nếu muốn
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

        // --- NGÂN SÁCH (MỚI) ---
        SectionLabel("MỨC NGÂN SÁCH (DỰ KIẾN)")
        OutlinedTextField(
            value = budgetInput,
            onValueChange = { budgetInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: 5 triệu, 500 USD, Tiết kiệm...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFF2196F3)) }, // Thêm icon Tiền
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SỞ THÍCH DU LỊCH (MỚI) ---
        SectionLabel("SỞ THÍCH DU LỊCH")
        OutlinedTextField(
            value = interestsInput,
            onValueChange = { interestsInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: Ẩm thực đường phố, ngắm cảnh...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF2196F3)) }, // Thêm icon Trái tim
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- NÚT BẤM ---
        Button(
            onClick = {
                if (destination.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập điểm đến!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                coroutineScope.launch {
                    try {
                        // 2. CẬP NHẬT LẠI PROMPT VỚI BIẾN MỚI
                        // Nếu user bỏ trống thì truyền chữ "Không yêu cầu cụ thể"
                        val finalBudget = if (budgetInput.isNotBlank()) budgetInput else "Không yêu cầu cụ thể"
                        val finalInterests = if (interestsInput.isNotBlank()) interestsInput else "Không yêu cầu cụ thể"

                        val prompt = "Hãy lên lịch trình du lịch đi $destination trong $duration ngày. " +
                                "Ngân sách dự kiến: $finalBudget. " +
                                "Sở thích/Yêu cầu đặc biệt: $finalInterests. " +
                                "Trình bày rõ ràng từng ngày."

                        Log.d("AI_DEBUG", "Câu lệnh gửi đi: $prompt")

                        val response = generativeModel.generateContent(prompt)

                        Log.d("AI_DEBUG", "Kết quả trả về: ${response.text}")

                        aiResult = response.text ?: "Không có kết quả"
                        showResultDialog = true
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
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tạo lịch trình bằng AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    // --- DIALOG KẾT QUẢ ---
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("Đóng") }
            },
            title = { Text("Lịch trình đề xuất ✨") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    MarkdownText(markdown = aiResult)
                }
            }
        )
    }
}

// Giữ lại SectionLabel, có thể XÓA SimpleFlowRow và InterestTag
@Composable
fun SectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true)
@Composable
fun AIPlannerPreview() {
    AIPlannerScreen()
}