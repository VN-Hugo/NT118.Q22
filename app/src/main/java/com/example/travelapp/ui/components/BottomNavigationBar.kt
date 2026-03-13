package com.example.travelapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)

@Composable
fun TravelBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            NavigationItem("HOME", "home", Icons.Default.Home),
            NavigationItem("EXPLORE", "explore", Icons.Default.Person),
            NavigationItem("TRIPS", "trips", Icons.Default.Place),
            NavigationItem("AI PLANNER", "ai_planner", Icons.Default.Star),
            NavigationItem("PROFILE", "profile", Icons.Default.Person)
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 9.sp)
                }
            )
        }
    }
}