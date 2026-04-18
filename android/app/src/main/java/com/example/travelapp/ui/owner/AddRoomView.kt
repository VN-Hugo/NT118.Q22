package com.example.travelapp.ui.owner

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
private val BrandTealColor = Color(0xFF005D67)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(
    onBack: () -> Unit,
    viewModel: AddRoomViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.onImagesSelected(uris)
    }

    LaunchedEffect(state) {
        if (state is AddRoomState.Success) {
            Toast.makeText(context, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show()
            onBack()
        } else if (state is AddRoomState.Error) {
            Toast.makeText(context, (state as AddRoomState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm hạng phòng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (state is AddRoomState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandTealColor)
                    } else {
                        TextButton(onClick = { viewModel.saveRoom() }) {
                            Text("LƯU", color = BrandTealColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Room Details
            OwnerInputField(
                label = "Tên hạng phòng",
                value = viewModel.typeName,
                onValueChange = { viewModel.typeName = it },
                placeholder = "Ví dụ: Deluxe Double Ocean View"
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OwnerInputField(
                        label = "Giá (đ/đêm)",
                        value = viewModel.price,
                        onValueChange = { viewModel.price = it },
                        placeholder = "1.200.000"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    OwnerInputField(
                        label = "Số lượng phòng",
                        value = viewModel.totalRooms,
                        onValueChange = { viewModel.totalRooms = it },
                        placeholder = "10"
                    )
                }
            }

            OwnerInputField(
                label = "Tiện ích phòng",
                value = viewModel.amenitiesText,
                onValueChange = { viewModel.amenitiesText = it },
                placeholder = "Wifi, Điều hòa, Bồn tắm... (ngăn cách bởi dấu phẩy)"
            )

            // Section 2: Photo Upload
            Text("Hình ảnh phòng", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            
            if (viewModel.selectedImages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFF2F4F5), RoundedCornerShape(12.dp))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Text("Bấm để chọn ảnh từ thư viện", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.selectedImages.forEach { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // Add more button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFFF2F4F5), RoundedCornerShape(8.dp))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = BrandTealColor)
                    }
                }
                TextButton(onClick = { viewModel.onImagesSelected(emptyList()) }) {
                    Text("Xóa tất cả ảnh", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
