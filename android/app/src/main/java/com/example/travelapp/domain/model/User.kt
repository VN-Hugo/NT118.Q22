package com.example.travelapp.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
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
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "avatarUrl" to avatarUrl,
            "role" to role,
            "favoriteIds" to favoriteIds,
            "fcmToken" to fcmToken,
            "isActive" to isActive,
            "createdAt" to createdAt
        )
    }
}