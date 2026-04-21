package com.example.travelapp.data.model

data class RoomType(
    val roomTypeId: String = "",
    val typeName: String = "",
    val price: Double = 0.0,
    val totalRooms: Int = 0,
    val amenities: List<String> = emptyList(),
    val images: List<String> = emptyList() // Bổ sung ảnh riêng cho từng loại phòng
)