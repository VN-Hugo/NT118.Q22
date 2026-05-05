package com.example.travelapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Review(
    @get:PropertyName("reviewId") @set:PropertyName("reviewId") var reviewId: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("bookId") @set:PropertyName("bookId") var bookId: String = "", // Để tránh review nhiều lần cho 1 đơn
    @get:PropertyName("proId") @set:PropertyName("proId") var proId: String = "",
    @get:PropertyName("username") @set:PropertyName("username") var username: String = "",
    @get:PropertyName("userAvatar") @set:PropertyName("userAvatar") var userAvatar: String = "",
    @get:PropertyName("rating") @set:PropertyName("rating") var rating: Int = 5,
    @get:PropertyName("comment") @set:PropertyName("comment") var comment: String = "",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis()
)