package com.example.travelapp.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OTPScreen(onBackClick: () -> Unit = {}, onVerifyClick: () -> Unit = {}) {
    // Lưu 4 số OTP (Bạn có thể tăng lên 6 nếu cần)
    var otpCode by remember { mutableStateOf(List(4) { "" }) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Hình nền Alps đồng bộ
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Lớp phủ tối
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Black.copy(0.4f), Color.Black.copy(0.9f)))
        ))

        // 3. Nút quay lại (Dùng bản AutoMirrored như bạn đã sửa)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 40.dp, start = 16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Xác thực mã OTP", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Text(
                "Vui lòng nhập mã 4 số đã được gửi tới email của bạn",
                color = Color.White.copy(0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
            )

            // 4. Hàng chứa các ô nhập OTP rời nhau
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpCode.forEachIndexed { index, value ->
                    OTPDigitBox(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                val newCode = otpCode.toMutableList()
                                newCode[index] = newValue
                                otpCode = newCode
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 5. Nút Xác nhận
            Button(
                onClick = onVerifyClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Xác nhận", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            TextButton(onClick = { /* Gửi lại mã */ }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Chưa nhận được mã? Gửi lại", color = Color.White)
            }
        }
    }
}

@Composable
fun RowScope.OTPDigitBox(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.weight(1f).aspectRatio(1f), // Tạo ô vuông
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(0.4f),
            cursorColor = Color.White
        )
    )
}