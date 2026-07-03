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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.travelapp.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen(
    onPropertyClick: (String) -> Unit = {},
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val savingState by viewModel.savingState.collectAsState()

    // Input state
    var destination by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(3) }
    var budgetInput by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }

    // Show result or input form
    val showResult = uiState is PlannerUiState.Success

    if (showResult) {
        val successState = uiState as PlannerUiState.Success
        PlanResultView(
            plan = successState.plan,
            properties = successState.matchedProperties,
            isSaving = savingState,
            onPropertyClick = onPropertyClick,
            onSave = {
                viewModel.savePlan(successState.plan)
                Toast.makeText(context, "Đã lưu kế hoạch!", Toast.LENGTH_SHORT).show()
            },
            onBack = { viewModel.resetState() }
        )
    } else {
        PlanInputForm(
            destination = destination,
            onDestinationChange = { destination = it },
            duration = duration,
            onDurationChange = { duration = it },
            budgetInput = budgetInput,
            onBudgetChange = { budgetInput = it },
            interestsInput = interestsInput,
            onInterestsChange = { interestsInput = it },
            uiState = uiState,
            onGenerate = {
                if (destination.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập điểm đến!", Toast.LENGTH_SHORT).show()
                    return@PlanInputForm
                }
                viewModel.generatePlan(destination, duration, budgetInput, interestsInput)
            }
        )
    }
}

// ========================
// INPUT FORM
// ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanInputForm(
    destination: String,
    onDestinationChange: (String) -> Unit,
    duration: Int,
    onDurationChange: (Int) -> Unit,
    budgetInput: String,
    onBudgetChange: (String) -> Unit,
    interestsInput: String,
    onInterestsChange: (String) -> Unit,
    uiState: PlannerUiState,
    onGenerate: () -> Unit
) {
    val isLoading = uiState is PlannerUiState.Interpreting || uiState is PlannerUiState.Generating
    val loadingMessage = when (uiState) {
        is PlannerUiState.Interpreting -> uiState.message
        is PlannerUiState.Generating -> uiState.message
        else -> ""
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
            onValueChange = onDestinationChange,
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
            IconButton(onClick = { if (duration > 1) onDurationChange(duration - 1) }, modifier = Modifier.background(Color.White, CircleShape).size(44.dp)) {
                Icon(Icons.Default.Remove, null, tint = Color(0xFF2196F3))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$duration", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("NGÀY", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { onDurationChange(duration + 1) }, modifier = Modifier.background(Color.White, CircleShape).size(44.dp)) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF2196F3))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- NGÂN SÁCH ---
        SectionLabel("MỨC NGÂN SÁCH (DỰ KIẾN)")
        OutlinedTextField(
            value = budgetInput,
            onValueChange = onBudgetChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: 5 triệu, 500 USD, Tiết kiệm...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.MonetizationOn, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SỞ THÍCH ---
        SectionLabel("SỞ THÍCH DU LỊCH")
        OutlinedTextField(
            value = interestsInput,
            onValueChange = onInterestsChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("VD: Ẩm thực đường phố, ngắm cảnh...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF2196F3)) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC), focusedContainerColor = Color(0xFFF8FAFC), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- NÚT BẤM ---
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(loadingMessage, fontSize = 14.sp, color = Color.White)
            } else {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tạo lịch trình bằng AI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Error display
        if (uiState is PlannerUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    (uiState as PlannerUiState.Error).message,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ========================
// PLAN RESULT VIEW
// ========================

@Composable
private fun PlanResultView(
    plan: Plan,
    properties: List<Property>,
    isSaving: Boolean,
    onPropertyClick: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val propertyMap = remember(properties) {
        properties.associateBy { it.proId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
            }
            Text(
                "Lịch trình ${plan.destination}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Plan summary card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF007BFF))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("📍 ${plan.destination}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("🗓 ${plan.duration} ngày", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("💰 ${plan.budget}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
                if (plan.interests.isNotBlank() && plan.interests != "Không yêu cầu cụ thể") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("❤️ ${plan.interests}", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        // Day-by-day itinerary
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            plan.days.forEach { day ->
                DayCard(
                    planDay = day,
                    propertyMap = propertyMap,
                    onPropertyClick = onPropertyClick
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save button at bottom
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp),
                enabled = !isSaving,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đang lưu...")
                } else {
                    Icon(Icons.Default.BookmarkAdd, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lưu kế hoạch", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ========================
// DAY CARD
// ========================

@Composable
private fun DayCard(
    planDay: PlanDay,
    propertyMap: Map<String, Property>,
    onPropertyClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day title
            Text(
                planDay.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activities
            planDay.activities.forEachIndexed { index, activity ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Time badge
                    if (activity.time.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Text(
                                activity.time,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(activity.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        if (activity.description.isNotBlank()) {
                            Text(activity.description, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
                        }
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            if (activity.location.isNotBlank()) {
                                Text("📍 ${activity.location}", fontSize = 11.sp, color = Color(0xFF757575))
                            }
                            if (activity.estimatedCost.isNotBlank()) {
                                if (activity.location.isNotBlank()) {
                                    Text(" · ", fontSize = 11.sp, color = Color(0xFF757575))
                                }
                                Text("💰 ${activity.estimatedCost}", fontSize = 11.sp, color = Color(0xFF757575))
                            }
                        }
                    }
                }

                if (index < planDay.activities.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = Color(0xFFF5F5F5)
                    )
                }
            }

            // Hotel recommendation card
            planDay.suggestedHotel?.let { hotel ->
                val property = propertyMap[hotel.propertyId]
                if (property != null || hotel.name.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE8EAF6))
                    Spacer(modifier = Modifier.height(12.dp))
                    HotelRecommendationCard(
                        suggestedHotel = hotel,
                        property = property,
                        onClick = {
                            if (property != null) {
                                onPropertyClick(property.proId)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ========================
// HOTEL RECOMMENDATION CARD
// ========================

@Composable
private fun HotelRecommendationCard(
    suggestedHotel: SuggestedHotel,
    property: Property?,
    onClick: () -> Unit
) {
    val isClickable = property != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel image (if available from DB)
            val imageUrl = property?.images?.firstOrNull()?.url
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = suggestedHotel.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                // Placeholder icon
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFCE93D8)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Hotel, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏨", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gợi ý lưu trú", fontSize = 11.sp, color = Color(0xFF7B1FA2), fontWeight = FontWeight.Bold)
                }
                Text(
                    suggestedHotel.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (suggestedHotel.rating > 0) {
                        Text("⭐ ${suggestedHotel.rating}", fontSize = 12.sp, color = Color(0xFF757575))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (suggestedHotel.pricePerNight > 0) {
                        Text(
                            "đ${String.format("%,.0f", suggestedHotel.pricePerNight)}/đêm",
                            fontSize = 12.sp,
                            color = Color(0xFF7B1FA2),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (isClickable) {
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = Color(0xFF7B1FA2),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ========================
// HELPER COMPOSABLES
// ========================

@Composable
fun SectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), modifier = Modifier.padding(bottom = 12.dp))
}