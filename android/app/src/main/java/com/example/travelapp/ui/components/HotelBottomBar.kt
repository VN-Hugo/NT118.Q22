package com.example.travelapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelapp.R

data class HotelNavigationItem(val title: String, val route: String, val iconRes: Int)

@Composable
fun HotelBottomBar(
    currentRoute: String,
     onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            HotelNavigationItem("TỔNG QUAN", "dashboard", R.drawable.ic_dashboard),
            HotelNavigationItem("KHÁCH SẠN", "hotels", R.drawable.ic_hotel_bed),
            HotelNavigationItem("ĐẶT PHÒNG", "bookings", R.drawable.ic_booking),
            HotelNavigationItem("PHÂN TÍCH", "analytics", R.drawable.ic_anaylytics),
            HotelNavigationItem("CÁ NHÂN", "profile", R.drawable.ic_profile)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) Color(0xFF005D67) else Color.Gray.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF005D67) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White
                )
            )
        }
    }
}
