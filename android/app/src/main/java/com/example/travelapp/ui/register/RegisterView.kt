package com.example.travelapp.ui.register

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.R
import com.example.travelapp.ui.login.AuthState
import com.example.travelapp.ui.login.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Role selection state
    val roles = listOf("USER", "HOTEL_OWNER")
    var selectedRole by remember { mutableStateOf(roles[0]) }

    val context = LocalContext.current
    val authState = viewModel.authState

    // --- CẬP NHẬT LUỒNG XÁC THỰC EMAIL Ở ĐÂY ---
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // 1. Hiển thị thông báo yêu cầu vào email
                Toast.makeText(
                    context,
                    "Đăng ký thành công! Vui lòng kiểm tra Email (hoặc Thư rác) để xác thực tài khoản.",
                    Toast.LENGTH_LONG
                ).show()

                // 2. Reset state để tránh lỗi khi quay lại
                viewModel.resetState()

                // 3. Đá về trang Login thay vì vào Home
                onBackClick()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f))
            )
        ))

        // 2. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Tăng Spacer để tránh bị đè bởi nút Back
            Text("Tạo tài khoản mới", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.Start))
            Text("Bắt đầu hành trình khám phá cùng chúng tôi", fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp))

            val isNotLoading = authState !is AuthState.Loading

            // --- Role Selection ---
            Text("Bạn là:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                roles.forEach { role ->
                    val isSelected = selectedRole == role
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = { selectedRole = role },
                                role = Role.RadioButton
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                        border = if (!isSelected) BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (role == "USER") "Khách du lịch" else "Chủ khách sạn",
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = isNotLoading,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = isNotLoading,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = isNotLoading,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận Mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = isNotLoading,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            viewModel.signUp(email, password, name, selectedRole)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = isNotLoading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("Đăng ký", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackClick, enabled = isNotLoading) {
                Text("Bạn đã có tài khoản? Đăng nhập", color = Color.White)
            }
        }

        // 3. Back Button (Đặt cuối cùng để nằm trên lớp Column)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.statusBarsPadding().padding(8.dp).align(Alignment.TopStart),
            enabled = authState !is AuthState.Loading
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}
