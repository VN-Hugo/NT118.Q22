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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { /* Handle back */ }
                )
                Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }

            when (val state = profileState) {
                is ProfileState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        user = state.user,
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
    onLogoutClick: () -> Unit,
    onEditAvatarClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Header Section (Avatar & Name)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                // Avatar
                if (user.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .border(4.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }

                // Edit Button
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onEditAvatarClick() },
                    shape = CircleShape,
                    color = Color(0xFF2196F3),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Avatar",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                user.fullName.ifEmpty { "User Name" },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                user.email,
                fontSize = 15.sp,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Stats Row (Trips, Countries, Miles) - Keep static for now
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(label = "Trips", value = "12", modifier = Modifier.weight(1f))
            StatCard(label = "Countries", value = "4", modifier = Modifier.weight(1f))
            StatCard(label = "Miles", value = "24k", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sections
        SectionTitle("TRAVEL ACTIVITY")
        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            title = "Wishlist",
            subtitle = "8 saved destinations",
            iconBgColor = Color(0xFFE3F2FD),
            iconColor = Color(0xFF2196F3)
        )
        ProfileMenuItem(
            icon = Icons.Outlined.Place,
            title = "Travel History",
            subtitle = "Past bookings and itineraries",
            iconBgColor = Color(0xFFE1F5FE),
            iconColor = Color(0xFF03A9F4)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("APPLICATION")
        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            subtitle = "Preferences and privacy",
            iconBgColor = Color(0xFFF5F5F5),
            iconColor = Color(0xFF616161)
        )
        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "Help Center",
            subtitle = "FAQs and customer support",
            iconBgColor = Color(0xFFF5F5F5),
            iconColor = Color(0xFF616161)
        )

        // Logout Button
        LogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE3F2FD).copy(alpha = 0.5f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF546E7A)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBgColor: Color,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = iconBgColor
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(14.dp)
            )
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color(0xFF263238))
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFEBEE)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.padding(14.dp)
            )
        }
        Text(
            "Logout",
            modifier = Modifier.padding(start = 16.dp),
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}