package com.example.travelapp.data.remote.dto

data class PropertyDTO(
    val proId: String = "",
    val ownerId: String = "", // Thêm trường này
    val name: String = "",
    val type: String = "hotel",
    val desId: String = "",
    val desName: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val description: String = "",
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val status: String = "PENDING", // Thêm trường này
    val tags: List<String> = emptyList(),
    val images: List<PropertyImageDTO> = emptyList(),
    val hotelInfo: HotelInfoDTO? = null,
    val activityInfo: ActivityInfoDTO? = null
)

data class PropertyImageDTO(
    val url: String = "",
    val isPrimary: Boolean = false
)

data class HotelInfoDTO(
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val policy: String = ""
)

data class ActivityInfoDTO(
    val duration: String = "",
    val maxPax: Int = 0
)