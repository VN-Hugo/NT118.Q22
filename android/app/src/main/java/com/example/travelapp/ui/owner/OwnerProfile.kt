package com.example.travelapp.ui.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.travelapp.R
import com.example.travelapp.data.model.User

// Bảng màu đồng bộ với thiết kế Owner
private val TealPrimaryColor = Color(0xFF005D67)
private val CardGrayColor = Color(0xFFF1F3F4)
private val VisaDarkColor = Color(0xFF1A1F24)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileScreen(
    onNavigate: (String) -> Unit,
    viewModel: OwnerProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = TealPrimaryColor)
                        Spacer(Modifier.width(8.dp))
                        Text("Tài khoản Owner", fontWeight = FontWeight.Bold)
                    }
                },
                actions = { 
                    IconButton(onClick = {}) { 
                        Icon(Icons.Default.Notifications, null, tint = TealPrimaryColor) 
                    } 
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = profileState) {
                is OwnerProfileState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimaryColor)
                    }
                }
                is OwnerProfileState.Success -> {
                    OwnerProfileContent(
                        padding = padding,
                        user = state.user,
                        viewModel = viewModel,
                        onNavigate = onNavigate
                    )
                }
                is OwnerProfileState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
            }

            // Overlay khi đang xử lý
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
}

@Composable
fun OwnerProfileContent(
    padding: PaddingValues,
    user: User,
    viewModel: OwnerProfileViewModel,
    onNavigate: (String) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(Modifier.padding(vertical = 12.dp)) {
                Text("Hồ sơ & Cài đặt", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TealPrimaryColor)
                Text("Quản lý thông tin doanh nghiệp và tài khoản cá nhân", fontSize = 14.sp, color = Color.Gray)
            }
        }

        // 1. Card Thông tin cá nhân
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (user.avatarUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(90.dp).clip(CircleShape).border(2.dp, CardGrayColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(Modifier.size(90.dp).background(CardGrayColor, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp), tint = Color.LightGray)
                                }
                            }
                            Surface(
                                modifier = Modifier.size(28.dp).clickable { galleryLauncher.launch("image/*") },
                                shape = CircleShape,
                                color = TealPrimaryColor,
                                border = BorderStroke(2.dp, Color.White)
                            ) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(user.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Chủ sở hữu • ${user.role}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    OwnerInputField("HỌ VÀ TÊN", viewModel.editFullName) { viewModel.editFullName = it }
                    OwnerInputField("SỐ ĐIỆN THOẠI", viewModel.editPhoneNumber) { viewModel.editPhoneNumber = it }
                    
                    // Email hiển thị (không cho sửa vì là định danh)
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Text("EMAIL LIÊN HỆ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(Modifier.height(6.dp))
                        Text(user.email, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.updateProfile() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Thông tin doanh nghiệp
        item { BusinessInfoCard() }

        // 3. Phương thức nhận tiền
        item { PaymentMethodCard() }

        // 4. AI Suggestion
        item { AiSuggestionFooter() }

        // 5. Nút Đăng xuất
        item {
            OutlinedButton(
                onClick = { viewModel.logout { onNavigate("logout") } },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất tài khoản", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun OwnerInputField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimaryColor,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun BusinessInfoCard() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Business, null, tint = TealPrimaryColor, modifier = Modifier.size(20.dp))
            Text("  Thông tin doanh nghiệp", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text("Pháp nhân: Editorial Hotel Co., Ltd", fontWeight = FontWeight.Bold)
                        Text("Mã số thuế: 010123456789", fontSize = 12.sp, color = Color.Gray)
                    }
                    Surface(color = Color(0xFFE0F2F1), shape = CircleShape) {
                        Text("ĐÃ XÁC MINH", fontSize = 9.sp, color = TealPrimaryColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(color = CardGrayColor, shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = TealPrimaryColor, modifier = Modifier.size(18.dp))
                        Text(" 123 Đường Lê Lợi, Quận 1, TP. HCM", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payments, null, tint = TealPrimaryColor, modifier = Modifier.size(20.dp))
            Text("  Phương thức nhận tiền", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = VisaDarkColor), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(Icons.Default.CreditCard, null, tint = Color.White)
                    Text("VISA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(24.dp))
                Text("**** **** **** 8892", color = Color.White, fontSize = 18.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("CHỦ THẺ OWNER", color = Color.White, fontSize = 12.sp)
                    Text("08/26", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AiSuggestionFooter() {
    Surface(
        color = TealPrimaryColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color.White.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Gợi ý từ AI Concierge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Tối ưu hóa giá phòng mùa cao điểm để tăng 20% doanh thu...", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }
        }
    }
}
