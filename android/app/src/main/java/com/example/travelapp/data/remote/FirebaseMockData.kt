package com.example.travelapp.data.remote

import com.example.travelapp.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseMockData @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    suspend fun createMockData() {
        // 1. Destinations
        val destinations = listOf(
            Destination("des1", "Đà Lạt", "Vietnam"),
            Destination("des2", "Phú Quốc", "Vietnam"),
            Destination("des3", "Hội An", "Vietnam")
        )
        destinations.forEach { db.collection("Destinations").document(it.desId).set(it).await() }

        // 2. Properties (Hotels)
        val properties = listOf(
            Property(
                proId = "pro1",
                ownerId = "owner123",
                name = "Terracotta Hotel & Resort",
                type = "hotel",
                desId = "des1",
                desName = "Đà Lạt",
                address = "Phân khu chức năng 7.9, KDL Hồ Tuyền Lâm",
                price = 1200000.0,
                averageRating = 4.5f,
                images = listOf(PropertyImage("https://images.unsplash.com/photo-1566073771259-6a8506099945", true))
            ),
            Property(
                proId = "pro2",
                ownerId = "owner123",
                name = "Vinpearl Resort & Spa",
                type = "hotel",
                desId = "des2",
                desName = "Phú Quốc",
                address = "Bãi Dài, Gành Dầu",
                price = 3500000.0,
                averageRating = 4.8f,
                images = listOf(PropertyImage("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b", true))
            )
        )
        properties.forEach { db.collection("Properties").document(it.proId).set(it).await() }

        // 3. Room Types for pro1
        val roomTypes = listOf(
            RoomType("rt1", "Deluxe Double", 1200000.0, 10, listOf("Wifi", "AC"), listOf(BedInfo("Double Bed", 1))),
            RoomType("rt2", "Superior Twin", 1000000.0, 5, listOf("Wifi"), listOf(BedInfo("Single Bed", 2)))
        )
        roomTypes.forEach { db.collection("Properties").document("pro1").collection("RoomTypes").document(it.roomTypeId).set(it).await() }

        // 4. Coupons
        val coupons = listOf(
            Coupon("cp1", "WELCOME50", 50000.0, "fixed", 200000.0, System.currentTimeMillis() + 864000000, true),
            Coupon("cp2", "SUMMER10", 10.0, "percent", 500000.0, System.currentTimeMillis() + 864000000, true)
        )
        coupons.forEach { db.collection("Coupons").document(it.couponId).set(it).await() }

        // 5. Reviews for pro1
        val reviews = listOf(
            Review("rev1", "user123", "Nguyễn Văn A", "", 5, "Khách sạn rất đẹp, phục vụ tốt!", System.currentTimeMillis()),
            Review("rev2", "user456", "Trần Thị B", "", 4, "View hồ Tuyền Lâm cực chill.", System.currentTimeMillis())
        )
        reviews.forEach { db.collection("Properties").document("pro1").collection("Reviews").document(it.reviewId).set(it).await() }

        // 6. Mock Users (One Owner, One Traveler)
        val owner = User(
            uid = "owner123",
            fullName = "Trần Minh Hoàng",
            email = "owner@example.com",
            role = "HOTEL_OWNER",
            avatarUrl = "https://i.pravatar.cc/150?u=owner123"
        )
        db.collection("Users").document(owner.uid).set(owner).await()

        val traveler = User(
            uid = "user123",
            fullName = "Nguyễn Văn A",
            email = "user@example.com",
            role = "USER",
            avatarUrl = "https://i.pravatar.cc/150?u=user123"
        )
        db.collection("Users").document(traveler.uid).set(traveler).await()

        // 7. Booking for traveler
        val booking = Booking(
            bookId = "book1",
            userId = "user123",
            proId = "pro1",
            proName = "Terracotta Hotel & Resort",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 172800000,
            totalPrice = 2400000.0,
            status = "confirmed",
            bookingType = "hotel",
            hotelBooking = HotelBookingDetails("rt1", 1)
        )
        db.collection("Bookings").document(booking.bookId).set(booking).await()
    }
}
