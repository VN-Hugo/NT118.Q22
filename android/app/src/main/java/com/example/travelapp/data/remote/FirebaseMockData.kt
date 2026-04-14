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
                description = "Khu nghỉ dưỡng Terracotta Đà Lạt nằm ẩn mình dưới những tán lá thông xanh biếc, soi bóng xuống mặt hồ Tuyền Lâm thơ mộng.",
                tags = listOf("Wifi", "Hồ bơi", "Spa", "Nhà hàng"),
                images = listOf(
                    PropertyImage("https://images.unsplash.com/photo-1566073771259-6a8506099945", true),
                    PropertyImage("https://images.unsplash.com/photo-1584132967334-10e028bd69f7", false)
                ),
                hotelInfo = HotelInfo("14:00", "12:00", "Không hút thuốc, không thú cưng.")
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
                description = "Vinpearl Resort & Spa Phú Quốc chào đón du khách bằng không gian nghỉ dưỡng sang trọng bậc nhất bên bãi biển hoang sơ.",
                tags = listOf("Bãi biển riêng", "Sân Golf", "Công viên nước", "Buffet"),
                images = listOf(
                    PropertyImage("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b", true),
                    PropertyImage("https://images.unsplash.com/photo-1544124499-58912cbddaad", false)
                ),
                hotelInfo = HotelInfo("14:00", "12:00", "Yêu cầu xuất trình CCCD khi nhận phòng.")
            )
        )
        properties.forEach { db.collection("Properties").document(it.proId).set(it).await() }

        // 3. Room Types for pro1
        val roomTypes = listOf(
            RoomType("rt1", "Deluxe Double", 1200000.0, 10, listOf("Wifi", "AC"), listOf(BedInfo("Double Bed", 1))),
            RoomType("rt2", "Superior Twin", 1000000.0, 5, listOf("Wifi"), listOf(BedInfo("Single Bed", 2)))
        )
        roomTypes.forEach { db.collection("Properties").document("pro1").collection("RoomTypes").document(it.roomTypeId).set(it).await() }

        // 4. Mock Users
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
    }
}
