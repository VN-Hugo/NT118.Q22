package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Boolean
    suspend fun checkRoomAvailability(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Int
    fun getUserBookings(userId: String): Flow<List<Booking>>
    fun getOwnerBookings(ownerId: String): Flow<List<Booking>>
    suspend fun getBookingById(bookId: String): Booking?
    suspend fun updateBookingStatus(bookId: String, status: String): Boolean
    suspend fun cancelBooking(bookId: String): Boolean
    
    // Mới: Lấy danh sách số lượng phòng đã đặt theo từng ngày để xem lịch
    fun getDailyOccupancy(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Flow<Map<Long, Int>>
}