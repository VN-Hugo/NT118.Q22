package com.example.travelapp.ui.owner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.ui.components.HotelBottomBar

// Bảng màu đồng bộ với thiết kế
private val TealPrimary = Color(0xFF004D40)
private val CardGray = Color(0xFFF1F3F4)
private val VisaDark = Color(0xFF1A1F24)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(24.dp), tint = TealPrimary) },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = TealPrimary) } }
            )
        },
        bottomBar = {
            HotelBottomBar(currentRoute = "profile", onNavigate = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text("Hồ sơ & Cài đặt", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TealPrimary)
                    Text("Quản lý tài khoản cá nhân và thông tin vận hành...", fontSize = 13.sp, color = Color.Gray)
                }
            }

            // 1. Card Thông tin cá nhân
            item {
                ProfileInfoCard()
            }

            // 2. Thông tin doanh nghiệp
            item {
                BusinessInfoCard()
            }

            // 3. Phương thức thanh toán (Thẻ Visa)
            item {
                PaymentMethodCard()
            }

            // 4. Lịch sử giao dịch
            item {
                TransactionHistoryCard()
            }

            // 5. AI Suggestion
            item {
                AiSuggestionFooter()
            }

            // 6. Nút Đăng xuất
            item {
                OutlinedButton(
                    onClick = { /* Logout */ },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    Text("  Đăng xuất tài khoản", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.size(80.dp).background(Color.LightGray, CircleShape)) // Avatar
                    Box(Modifier.size(24.dp).background(TealPrimary, CircleShape).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Trần Minh Hoàng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Chủ sở hữu • Editorial Hotel Group", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            InputField("HỌ VÀ TÊN", "Trần Minh Hoàng")
            InputField("EMAIL LIÊN HỆ", "hoang.tran@editorialconcierge.com")
            InputField("SỐ ĐIỆN THOẠI", "+84 901 234 567")
        }
    }
}

@Composable
fun BusinessInfoCard() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, null, tint = TealPrimary)
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
                        Text("ĐÃ XÁC MINH", fontSize = 9.sp, color = TealPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(color = CardGray, shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                        Text(" 123 Đường Lê Lợi, Quận 1, TP. HCM", fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Gói dịch vụ Card
                Surface(color = TealPrimary, shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("GÓI DỊCH VỤ", fontSize = 10.sp, color = Color.White.copy(0.7f))
                        Text("Elite Concierge", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Hết hạn: 12/2024", fontSize = 11.sp, color = Color.White.copy(0.7f))
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f)), modifier = Modifier.fillMaxWidth()) {
                            Text("NÂNG CẤP", color = Color.White)
                        }
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
            Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Person, null)
        }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = VisaDark), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(Icons.Default.Person, null, tint = Color.White)
                    Text("VISA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(24.dp))
                Text("**** **** **** 8892", color = Color.White, fontSize = 18.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TRẦN MINH HOÀNG", color = Color.White, fontSize = 12.sp)
                    }
                    Text("08/26", color = Color.White, fontSize = 12.sp)
                }
            }
        }
        TextButton(onClick = {}) { Text("+ Thêm thẻ mới", color = Color.Gray) }
    }
}

@Composable
fun TransactionHistoryCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Lịch sử giao dịch", fontWeight = FontWeight.Bold)
            TransactionItem("Phí dịch vụ Tháng 10", "15/10/2023", "-2,500,000đ")
            TransactionItem("Dịch vụ AI Curator", "02/10/2023", "-500,000đ")
            TextButton(onClick = {}) { Text("Xem tất cả hoá đơn →", fontSize = 12.sp, color = TealPrimary) }
        }
    }
}

@Composable
fun TransactionItem(title: String, date: String, amount: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(CardGray, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(date, fontSize = 11.sp, color = Color.Gray)
        }
        Text(amount, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun InputField(label: String, value: String) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Surface(color = CardGray, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
            Text(value, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
        }
    }
}

@Composable
fun AiSuggestionFooter() {
    Surface(
        color = TealPrimary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color.White.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Gợi ý từ AI Concierge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Bạn có thể tiết kiệm 15% phí vận hành...", color = Color.White.copy(0.8f), fontSize = 11.sp)
            }
        }
    }
}
