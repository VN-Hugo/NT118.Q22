package com.example.travelapp.domain.repository

import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(type: String? = null): Flow<List<Property>>
    suspend fun getPropertyById(proId: String): Property?
    fun searchProperties(query: String): Flow<List<Property>>
    
    // Thêm các phương thức để Owner có thể quản lý
    suspend fun saveProperty(property: Property): Boolean
    suspend fun saveRoomType(proId: String, roomType: RoomType): Boolean
    fun getRoomTypes(proId: String): Flow<List<RoomType>>
}