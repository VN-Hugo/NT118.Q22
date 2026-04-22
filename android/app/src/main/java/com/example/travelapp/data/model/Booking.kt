package com.example.travelapp.data.model

data class Booking(
    val bookId: String = "",
    val userId: String = "",
    val proId: String = "",
    val proName: String = "", // Phi chuẩn hóa để hiển thị nhanh
    val proImage: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val totalPrice: Double = 0.0,
    val status: String = "pending", // pending, confirmed, cancelled
    val bookingType: String = "hotel",
    val countAdult: Int = 0,
    val countChild: Int = 0,
    // Dữ liệu cụ thể
    val hotelBooking: HotelBookingDetails? = null,
    val activityBooking: ActivityBookingDetails? = null
)

data class HotelBookingDetails(
    val roomTypeId: String = "",
    val quantity: Int = 1
)

data class ActivityBookingDetails(
    val slotId: String = "",
    val pax: Int = 1
)