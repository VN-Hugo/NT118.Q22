package com.example.travelapp.data.repository

import android.net.Uri
import com.example.travelapp.data.model.Booking
import com.example.travelapp.data.model.Property
import com.example.travelapp.data.model.RoomType
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(type: String? = null): Flow<List<Property>>
    suspend fun getPropertyById(proId: String): Property?
    fun searchProperties(query: String): Flow<List<Property>>
    
    suspend fun saveProperty(property: Property): String?
    suspend fun saveRoomType(proId: String, roomType: RoomType): Boolean
    fun getRoomTypes(proId: String): Flow<List<RoomType>>
    suspend fun deleteRoomType(proId: String, roomTypeId: String): Boolean

    suspend fun uploadPropertyImage(path: String, uri: Uri): String?

    suspend fun checkRoomAvailability(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Int
    suspend fun createBooking(booking: Booking): Boolean

    suspend fun updatePropertyStatus(proId: String, status: String): Boolean
    suspend fun deleteProperty(proId: String): Boolean
}