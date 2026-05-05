package com.example.travelapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class RoomType(
    @get:PropertyName("roomTypeId") @set:PropertyName("roomTypeId") var roomTypeId: String = "",
    @get:PropertyName("typeName") @set:PropertyName("typeName") var typeName: String = "",
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("totalRooms") @set:PropertyName("totalRooms") var totalRooms: Int = 0,
    @get:PropertyName("amenities") @set:PropertyName("amenities") var amenities: List<String> = emptyList(),
    @get:PropertyName("images") @set:PropertyName("images") var images: List<String> = emptyList()
)