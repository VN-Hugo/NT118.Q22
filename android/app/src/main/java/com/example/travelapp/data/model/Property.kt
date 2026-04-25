package com.example.travelapp.data.model

data class Property(
    val proId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = "hotel", // "hotel" hoặc "activity"
    val desId: String = "",
    val desName: String = "",
    val address: String = "",
    val latitude: Double = 10.762622,  // Tọa độ mặc định (ví dụ TP.HCM)
    val longitude: Double = 106.660172,
    val description: String = "",
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val price: Double = 0.0,
    val status: String = "PENDING", // "PENDING" (Chờ duyệt), "APPROVED" (Hoạt động), "REJECTED" (Từ chối)
    val tags: List<String> = emptyList(),
    val images: List<PropertyImage> = emptyList(),
    // Thông tin mở rộng tùy theo type
    val hotelInfo: HotelInfo? = null,
    val activityInfo: ActivityInfo? = null,
    val defaultPrice: Double = 0.0
)

data class PropertyImage(
    val url: String = "",
    val isPrimary: Boolean = false
)

data class HotelInfo(
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val policy: String = ""
)

data class ActivityInfo(
    val duration: String = "",
    val maxPax: Int = 0
)