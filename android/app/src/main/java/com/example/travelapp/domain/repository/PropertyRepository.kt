package com.example.travelapp.domain.repository

import android.net.Uri
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(type: String? = null): Flow<List<Property>>
    suspend fun getPropertyById(proId: String): Property?
    fun searchProperties(query: String): Flow<List<Property>>
    
    suspend fun saveProperty(property: Property): Boolean
    suspend fun saveRoomType(proId: String, roomType: RoomType): Boolean
    fun getRoomTypes(proId: String): Flow<List<RoomType>>

    // Thêm chức năng upload ảnh
    suspend fun uploadPropertyImage(path: String, uri: Uri): String?
}