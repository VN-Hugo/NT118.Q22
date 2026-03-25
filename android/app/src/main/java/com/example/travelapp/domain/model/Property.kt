package com.example.travelapp.domain.model

data class Property(
    val proId: String = "",
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
    val tags: List<String> = emptyList(),
    val images: List<PropertyImage> = emptyList(),
    val hotelInfo: HotelInfo? = null,
    val activityInfo: ActivityInfo? = null
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "proId" to proId,
            "name" to name,
            "type" to type,
            "desId" to desId,
            "desName" to desName,
            "address" to address,
            "lat" to lat,
            "lng" to lng,
            "description" to description,
            "averageRating" to averageRating,
            "reviewCount" to reviewCount,
            "tags" to tags,
            // Chuyển danh sách object thành danh sách các Map
            "images" to images.map { it.toMap() }
        )

        // Chỉ thêm vào Map nếu dữ liệu không null (để tránh ghi đè null lên Firestore)
        hotelInfo?.let { map["hotelInfo"] = it.toMap() }
        activityInfo?.let { map["activityInfo"] = it.toMap() }

        return map
    }
}

data class PropertyImage(
    val url: String = "",
    val isPrimary: Boolean = false
) {
    fun toMap() = mapOf(
        "url" to url,
        "isPrimary" to isPrimary
    )
}

data class HotelInfo(
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val policy: String = ""
) {
    fun toMap() = mapOf(
        "checkInTime" to checkInTime,
        "checkOutTime" to checkOutTime,
        "policy" to policy
    )
}

data class ActivityInfo(
    val duration: String = "",
    val maxPax: Int = 0
) {
    fun toMap() = mapOf(
        "duration" to duration,
        "maxPax" to maxPax
    )
}