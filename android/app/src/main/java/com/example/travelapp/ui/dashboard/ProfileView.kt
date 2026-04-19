package com.example.travelapp.ui.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.domain.model.User

@Composable
fun ProfileScreen(
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(context, it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle back */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
                Text("Tài khoản của tôi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
                }
            }

            when (val state = profileState) {
                is ProfileState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2196F3))
                    }
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        user = state.user,
                        viewModel = viewModel,
                        onLogoutClick = { viewModel.logout(onLogoutSuccess) },
                        onEditAvatarClick = { launcher.launch("image/*") }
                    )
                }
                is ProfileState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }

        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    viewModel: ProfileViewModel,
    onLogoutClick: () -> Unit,
    onEditAvatarClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Header Section (Avatar & Basic Info)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (user.avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color(0xFFF1F3F4), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD))
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color(0xFF2196F3))
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onEditAvatarClick() },
                        shape = CircleShape,
                        color = Color(0xFF2196F3),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Icon(Icons.Default.Person, "Đổi ảnh", tint = Color.White, modifier = Modifier.padding(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(user.fullName.ifEmpty { "Khách du lịch" }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user.email, fontSize = 14.sp, color = Color.Gray)
            }
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(label = "Chuyến đi", value = "12", modifier = Modifier.weight(1f))
            StatCard(label = "Quốc gia", value = "4", modifier = Modifier.weight(1f))
            StatCard(label = "Điểm thưởng", value = "2.4k", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Info Edit Section
        SectionTitle("THÔNG TIN CÁ NHÂN")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                EditField(label = "Họ và tên", value = viewModel.editFullName, onValueChange = { viewModel.editFullName = it })
                Spacer(modifier = Modifier.height(12.dp))
                EditField(label = "Số điện thoại", value = viewModel.editPhoneNumber, onValueChange = { viewModel.editPhoneNumber = it })
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.updateProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Sections
        SectionTitle("HOẠT ĐỘNG DU LỊCH")
        ProfileMenuItem(Icons.Default.FavoriteBorder, "Danh sách yêu thích", "8 địa điểm đã lưu", Color(0xFFE3F2FD), Color(0xFF2196F3))
        ProfileMenuItem(Icons.Outlined.Add, "Lịch sử chuyến đi", "Các đơn đặt và hành trình cũ", Color(0xFFF1F8E9), Color(0xFF4CAF50))

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        LogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun EditField(label: String, value: androidx.compose.ui.text.input.TextFieldValue, onValueChange: (androidx.compose.ui.text.input.TextFieldValue) -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2196F3), unfocusedBorderColor = Color(0xFFE0E0E0))
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.height(85.dp), shape = RoundedCornerShape(20.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFF1F3F4))) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF455A64))
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, iconBgColor: Color, iconColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp), color = iconBgColor) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.padding(12.dp))
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF263238))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().padding(24.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = Color(0xFFFFEBEE), border = BorderStroke(1.dp, Color(0xFFFFCDD2))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất tài khoản", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
