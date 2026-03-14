package com.example.travelapp.model

import androidx.compose.ui.graphics.Color


interface Place {
    val id: String
    val name: String
    val location: String
    val price: String
    val rating: String
    val imageBg: Color // Sau này đổi thành String (URL) hoặc Int (Resource ID)
}

/**
 * 2. Data Class cho Khách sạn
 */
data class Hotel(
    override val id: String = "",
    override val name: String,
    override val location: String,
    override val price: String,
    override val rating: String,
    override val imageBg: Color,
    val starCount: Int,         // Riêng khách sạn: 3*, 4*, 5*
    val hasWifi: Boolean = true
) : Place

/**
 * 3. Data Class cho Nhà hàng
 */
data class Restaurant(
    override val id: String = "",
    override val name: String,
    override val location: String,
    override val price: String,
    override val rating: String,
    override val imageBg: Color,
    val cuisineType: String,
    val isOpening: Boolean = true
) : Place

/**
 * 4. Data Class cho Địa điểm tham quan
 */
data class Attraction(
    override val id: String = "",
    override val name: String,
    override val location: String,
    override val price: String,
    override val rating: String,
    override val imageBg: Color,
    val openingHours: String
) : Place