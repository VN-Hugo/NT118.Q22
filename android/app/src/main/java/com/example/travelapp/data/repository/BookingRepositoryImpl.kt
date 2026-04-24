package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BookingRepository {

    private val bookingsCollection = db.collection("Bookings")

    override suspend fun createBooking(booking: Booking): Boolean {
        return try {
            val docRef = bookingsCollection.document()
            val finalBooking = booking.copy(bookId = docRef.id)
            docRef.set(finalBooking).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkRoomAvailability(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Int {
        return try {
            // 1. Lấy tổng số phòng từ Property details
            val roomDoc = db.collection("Properties").document(proId)
                .collection("RoomTypes").document(roomTypeId).get().await()
            val total = roomDoc.getLong("totalRooms")?.toInt() ?: 0

            // 2. Tìm các đơn đặt phòng bị trùng lịch (status: confirmed hoặc pending)
            val bookingsSnap = bookingsCollection
                .whereEqualTo("proId", proId)
                .whereEqualTo("hotelBooking.roomTypeId", roomTypeId)
                .whereIn("status", listOf("confirmed", "pending"))
                .get().await()

            val occupiedRooms = bookingsSnap.documents.filter { doc ->
                val bStart = doc.getLong("startDate") ?: 0L
                val bEnd = doc.getLong("endDate") ?: 0L
                // Logic giao thoa ngày: (StartA < EndB) AND (EndA > StartB)
                startDate < bEnd && endDate > bStart
            }.sumOf { (it.get("hotelBooking.quantity") as? Long)?.toInt() ?: 1 }

            (total - occupiedRooms).coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }

    override fun getUserBookings(userId: String): Flow<List<Booking>> = callbackFlow {
        val subscription = bookingsCollection
            .whereEqualTo("userId", userId)
            .orderBy("startDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val bookings = it.toObjects(Booking::class.java)
                    trySend(bookings)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getBookingById(bookId: String): Booking? {
        return try {
            val doc = bookingsCollection.document(bookId).get().await()
            doc.toObject(Booking::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun cancelBooking(bookId: String): Boolean {
        return try {
            bookingsCollection.document(bookId).update("status", "cancelled").await()
            true
        } catch (e: Exception) {
            false
        }
    }
}