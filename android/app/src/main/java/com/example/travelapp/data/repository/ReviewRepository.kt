package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun submitReview(proId: String, review: Review): Boolean
    fun getReviewsByProperty(proId: String): Flow<List<Review>>
    fun getReviewsByUser(userId: String): Flow<List<Review>> // Thêm hàm này để tối ưu TripView
    suspend fun hasUserReviewed(bookId: String): Boolean
}