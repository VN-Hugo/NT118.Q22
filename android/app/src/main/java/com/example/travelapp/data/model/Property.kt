package com.example.travelapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Property(
    @get:PropertyName("proId") @set:PropertyName("proId") var proId: String = "",
    @get:PropertyName("ownerId") @set:PropertyName("ownerId") var ownerId: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("type") @set:PropertyName("type") var type: String = "hotel",
    @get:PropertyName("desId") @set:PropertyName("desId") var desId: String = "",
    @get:PropertyName("desName") @set:PropertyName("desName") var desName: String = "",
    @get:PropertyName("address") @set:PropertyName("address") var address: String = "",
    @get:PropertyName("latitude") @set:PropertyName("latitude") var latitude: Double = 10.762622,
    @get:PropertyName("longitude") @set:PropertyName("longitude") var longitude: Double = 106.660172,
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("averageRating") @set:PropertyName("averageRating") var averageRating: Float = 0f,
    @get:PropertyName("reviewCount") @set:PropertyName("reviewCount") var reviewCount: Int = 0,
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("status") @set:PropertyName("status") var status: String = "PENDING",
    @get:PropertyName("tags") @set:PropertyName("tags") var tags: List<String> = emptyList(),
    @get:PropertyName("images") @set:PropertyName("images") var images: List<PropertyImage> = emptyList(),
    @get:PropertyName("hotelInfo") @set:PropertyName("hotelInfo") var hotelInfo: HotelInfo? = null,
    @get:PropertyName("activityInfo") @set:PropertyName("activityInfo") var activityInfo: ActivityInfo? = null
)

data class PropertyImage(
    @get:PropertyName("url") @set:PropertyName("url") var url: String = "",
    @get:PropertyName("isPrimary") @set:PropertyName("isPrimary") var isPrimary: Boolean = false
)

data class HotelInfo(
    @get:PropertyName("checkInTime") @set:PropertyName("checkInTime") var checkInTime: String = "",
    @get:PropertyName("checkOutTime") @set:PropertyName("checkOutTime") var checkOutTime: String = "",
    @get:PropertyName("policy") @set:PropertyName("policy") var policy: String = ""
)

data class ActivityInfo(
    @get:PropertyName("duration") @set:PropertyName("duration") var duration: String = "",
    @get:PropertyName("maxPax") @set:PropertyName("maxPax") var maxPax: Int = 0
)