package com.example.travelapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.R

data class NavigationItem(val title: String, val route: String, val iconRes: Int)

@Composable
fun TravelBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            NavigationItem("HOME", "home", R.drawable.ic_home),
            NavigationItem("EXPLORE", "explore", R.drawable.ic_explore),
            NavigationItem("TRIPS", "trips", R.drawable.ic_trips),
            NavigationItem("AI PLANNER", "ai_planner", R.drawable.ic_my_ai),
            NavigationItem("PROFILE", "profile", R.drawable.ic_profile)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    // SỬA Ở ĐÂY: Thêm modifier để khống chế size
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.title,
                        modifier = androidx.compose.ui.Modifier.size(24.dp), // Size chuẩn Material
                        tint = if (isSelected) Color.Black else Color.Gray.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp,
                        // Thêm FontWeight để chữ trông sắc nét hơn
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                },
                // SỬA Ở ĐÂY: Loại bỏ hiệu ứng màu nền (indicator) nếu bạn muốn tối giản như icon cũ
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White // Bỏ cái vòng tròn màu nền khi click
                )
            )
        }
    }
}