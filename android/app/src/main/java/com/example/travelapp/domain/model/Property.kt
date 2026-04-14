package com.example.travelapp.domain.model

data class Property(
    val proId: String = "",
    val name: String = "",
    val type: String = "hotel", // "hotel" hoặc "activity"
    val desId: String = "",
    val desName: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val description: String = "",
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val tags: List<String> = emptyList(),
    val images: List<PropertyImage> = emptyList(),
    // Thông tin mở rộng tùy theo type
    val hotelInfo: HotelInfo? = null,
    val activityInfo: ActivityInfo? = null
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