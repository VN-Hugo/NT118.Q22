package com.example.travelapp.domain.model

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val role: String = "USER",
    val favoriteIds: List<String> = emptyList(),
    val fcmToken: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)