package com.example.travelapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val role: String = "USER", // "USER", "HOTEL_OWNER"
    val favoriteIds: List<String> = emptyList(),
    val fcmToken: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)