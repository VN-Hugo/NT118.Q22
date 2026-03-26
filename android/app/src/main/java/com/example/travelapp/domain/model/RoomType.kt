package com.example.travelapp.domain.model

data class RoomType(
    val roomTypeId: String = "",
    val typeName: String = "",
    val price: Double = 0.0,
    val totalRooms: Int = 0,
    val amenities: List<String> = emptyList(),
    val beds: List<BedInfo> = emptyList()
)

data class BedInfo(
    val bedName: String = "",
    val count: Int = 1
)
