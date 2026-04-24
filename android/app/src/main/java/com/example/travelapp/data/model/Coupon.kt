package com.example.travelapp.data.model

data class Coupon(
    val couponId: String = "",
    val code: String = "",
    val discountValue: Double = 0.0,
    val discountType: String = "percent", // "percent" hoặc "fixed"
    val minOrderValue: Double = 0.0,
    val endDate: Long = 0L,
    val isActive: Boolean = true
)
