package com.example.travelapp.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke // Đã thêm import này
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

import com.example.travelapp.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Hình nền
        Image(
            painter = painterResource(id = R.drawable.background), // Đã đổi tên theo ý bạn
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Lớp phủ Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f), // Đổi sang tối một chút để dễ đọc chữ trắng
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 500f
                    )
                )
        )

        // 3. Nội dung
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Chào mừng bạn \ntới Travel App",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 48.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Button(
                onClick = { /* Xử lý */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Tạo tài khoản mới", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bạn đã có tài khoản",
                color = Color.White,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Dùng HorizontalDivider thay cho Divider cũ của Material3
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f))
                Text(" Đăng nhập bằng ", color = Color.White, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sửa lỗi mismatch bằng cách chỉ định rõ tham số modifier
                SocialButton(
                    iconRes = R.drawable.ic_apple,
                    text = "Apple",
                    modifier = Modifier.weight(1f)
                )
                SocialButton(
                    iconRes = R.drawable.ic_google,
                    text = "Google",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SocialButton(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier // Gán mặc định để linh hoạt hơn
) {
    OutlinedButton(
        onClick = { /* Xử lý */ },
        modifier = modifier.height(56.dp), // Chú ý: dùng 'modifier' biến truyền vào
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
        contentPadding = PaddingValues(0.dp) // Đảm bảo icon và chữ không bị lệch
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}