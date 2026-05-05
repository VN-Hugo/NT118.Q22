package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewRepository {

    private val reviewsCollection = db.collection("Reviews")
    private val propertiesCollection = db.collection("Properties")

    override suspend fun submitReview(proId: String, review: Review): Boolean {
        if (proId.isEmpty()) return false
        
        return try {
            db.runTransaction { transaction ->
                // 1. ĐỌC TRƯỚC: Kiểm tra Property tồn tại
                val propertyRef = propertiesCollection.document(proId)
                val propertySnap = transaction.get(propertyRef)
                
                // 2. GHI SAU: Tạo bài đánh giá mới
                val reviewRef = reviewsCollection.document()
                val finalReview = review.copy(reviewId = reviewRef.id, proId = proId)
                transaction.set(reviewRef, finalReview)

                // 3. Cập nhật điểm trung bình (chỉ khi Property thực sự tồn tại)
                if (propertySnap.exists()) {
                    val property = propertySnap.toObject(Property::class.java)
                    if (property != null) {
                        val newCount = property.reviewCount + 1
                        val newAverage = ((property.averageRating * property.reviewCount) + review.rating) / newCount
                        
                        transaction.update(propertyRef, mapOf(
                            "averageRating" to newAverage,
                            "reviewCount" to newCount
                        ))
                    }
                }
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getReviewsByProperty(proId: String): Flow<List<Review>> = callbackFlow {
        val subscription = reviewsCollection
            .whereEqualTo("proId", proId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects(Review::class.java)) }
            }
        awaitClose { subscription.remove() }
    }

    override fun getReviewsByUser(userId: String): Flow<List<Review>> = callbackFlow {
        val subscription = reviewsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects(Review::class.java)) }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun hasUserReviewed(bookId: String): Boolean {
        return try {
            val query = reviewsCollection.whereEqualTo("bookId", bookId).get().await()
            !query.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}