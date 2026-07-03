package com.example.travelapp.data.model

data class Plan(
    val planId: String = "",
    val userId: String = "",
    val destination: String = "",
    val duration: Int = 0,
    val budget: String = "",
    val interests: String = "",
    val days: List<PlanDay> = emptyList(),
    val suggestedPropertyIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "saved"  // saved, archived
)

data class PlanDay(
    val dayNumber: Int = 0,
    val title: String = "",       // e.g., "Ngày 1: Khám phá trung tâm Đà Lạt"
    val activities: List<PlanActivity> = emptyList(),
    val suggestedHotel: SuggestedHotel? = null
)

data class PlanActivity(
    val time: String = "",        // e.g., "08:00"
    val title: String = "",       // e.g., "Tham quan Hồ Xuân Hương"
    val description: String = "",
    val estimatedCost: String = "",
    val location: String = ""
)

data class SuggestedHotel(
    val propertyId: String = "",  // Links to Property in our DB
    val name: String = "",
    val pricePerNight: Double = 0.0,
    val rating: Float = 0f
)
