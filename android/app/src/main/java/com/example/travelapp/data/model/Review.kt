package com.example.travelapp.data.model

data class Review(
    val reviewId: String = "",
    val userId: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)