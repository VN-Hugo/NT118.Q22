package com.example.travelapp.domain.repository

import com.example.travelapp.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(type: String? = null): Flow<List<Property>>
    suspend fun getPropertyById(proId: String): Property?
    fun searchProperties(query: String): Flow<List<Property>>
}