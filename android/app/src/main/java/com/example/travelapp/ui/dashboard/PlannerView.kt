package com.example.travelapp.ui.dashboard

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.utils.Secrets
import com.google.ai.client.generativeai.GenerativeModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var destination by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(3) }
    var budgetInput by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }

    var aiResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = Secrets.GEMINI_API_KEY
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Trợ lý Du lịch AI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(48.dp))
        }

        Text(text = "Lên kế hoạch chuyến đi", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
        Text(text = "Hãy để AI thiết kế lịch trình hoàn hảo dựa trên sở thích của bạn.", fontSize = 15.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(32.dp))

        SectionLabel("ĐIỂM ĐẾN")
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Bạn muốn đi đâu?", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("SỐ NGÀY LƯU TRÚ")
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF8FAFC)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { if (duration > 1) duration-- }, 
                modifier = Modifier.background(Color.White, CircleShape).size(44.dp)
            ) {
                Icon(Icons.Default.Remove, null, tint = Color(0xFF2196F3))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$duration", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("NGÀY", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = { duration++ }, 
                modifier = Modifier.background(Color.White, CircleShape).size(44.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF2196F3))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("MỨC NGÂN SÁCH (DỰ KIẾN)")
        OutlinedTextField(
            value = budgetInput,
            onValueChange = { budgetInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: 5 triệu, Tiết kiệm, Sang chảnh...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel("SỞ THÍCH DU LỊCH")
        OutlinedTextField(
            value = interestsInput,
            onValueChange = { interestsInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: Ẩm thực, Nghỉ dưỡng, Ngắm cảnh...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

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
                        val finalBudget = budgetInput.ifBlank { "Không yêu cầu cụ thể" }
                        val finalInterests = interestsInput.ifBlank { "Không yêu cầu cụ thể" }

                        val prompt = "Hãy lên lịch trình du lịch chi tiết đi $destination trong $duration ngày. " +
                                "Ngân sách dự kiến: $finalBudget. " +
                                "Sở thích: $finalInterests. " +
                                "Hãy trình bày rõ ràng từng ngày bằng định dạng Markdown tiếng Việt."

                        val response = generativeModel.generateContent(prompt)
                        aiResult = response.text ?: "Không có kết quả"
                        showResultDialog = true
                    } catch (e: Exception) {
                        Log.e("AI_ERROR", e.message ?: "Unknown Error")
                        aiResult = "Lỗi: Không thể kết nối với AI. Vui lòng kiểm tra lại mạng hoặc API Key."
                        showResultDialog = true
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
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

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("Đóng") }
            },
            title = { Text("Lịch trình đề xuất ✨", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    MarkdownText(
                        markdown = aiResult,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), modifier = Modifier.padding(bottom = 8.dp))
}
