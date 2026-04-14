package com.example.travelapp.data.repository

import com.example.travelapp.data.mapper.toDTO
import com.example.travelapp.data.mapper.toDomain
import com.example.travelapp.data.remote.dto.PropertyDTO
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor() : PropertyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val propertiesCollection = db.collection("Properties")

    override fun getProperties(type: String?): Flow<List<Property>> = callbackFlow {
        val query = if (type != null) {
            propertiesCollection.whereEqualTo("type", type)
        } else {
            propertiesCollection
        }
        
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val propertyDTOs = snapshot.toObjects(PropertyDTO::class.java)
                val properties = propertyDTOs.map { it.toDomain() }
                trySend(properties)
            }
        }
        awaitClose { subscription.remove() }
    }
    
    override suspend fun getPropertyById(proId: String): Property? {
        return try {
            val doc = propertiesCollection.document(proId).get().await()
            doc.toObject(PropertyDTO::class.java)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    override fun searchProperties(query: String): Flow<List<Property>> = callbackFlow {
        val subscription = propertiesCollection
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val propertyDTOs = snapshot.toObjects(PropertyDTO::class.java)
                    val properties = propertyDTOs.map { it.toDomain() }
                    trySend(properties)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveProperty(property: Property): Boolean {
        return try {
            val dto = property.toDTO()
            val docRef = if (dto.proId.isEmpty()) {
                propertiesCollection.document()
            } else {
                propertiesCollection.document(dto.proId)
            }
            val finalDto = dto.copy(proId = docRef.id)
            docRef.set(finalDto).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun saveRoomType(proId: String, roomType: RoomType): Boolean {
        return try {
            val roomRef = if (roomType.roomTypeId.isEmpty()) {
                propertiesCollection.document(proId).collection("RoomTypes").document()
            } else {
                propertiesCollection.document(proId).collection("RoomTypes").document(roomType.roomTypeId)
            }
            val finalRoom = roomType.copy(roomTypeId = roomRef.id)
            roomRef.set(finalRoom).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getRoomTypes(proId: String): Flow<List<RoomType>> = callbackFlow {
        val subscription = propertiesCollection.document(proId).collection("RoomTypes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val rooms = snapshot.toObjects(RoomType::class.java)
                    trySend(rooms)
                }
            }
        awaitClose { subscription.remove() }
    }
}