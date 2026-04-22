package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    // Tạo đơn đặt phòng mới
    suspend fun createBooking(booking: Booking): Boolean
    
    // Kiểm tra tính khả dụng (còn phòng trống không)
    suspend fun checkRoomAvailability(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Int
    
    // Lấy lịch sử đặt phòng của người dùng
    fun getUserBookings(userId: String): Flow<List<Booking>>
    
    // Lấy chi tiết một đơn đặt
    suspend fun getBookingById(bookId: String): Booking?
    
    // Hủy đơn đặt phòng
    suspend fun cancelBooking(bookId: String): Boolean
}