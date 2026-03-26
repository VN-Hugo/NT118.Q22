package com.example.travelapp.data.remote.dto

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDTO(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val role: String = "USER",
    val favoriteIds: List<String> = listOf(),
    val fcmToken: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
