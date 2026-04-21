package com.example.travelapp.data.repository

import android.net.Uri
import com.example.travelapp.data.mapper.toDTO
import com.example.travelapp.data.mapper.toDomain
import com.example.travelapp.data.remote.dto.PropertyDTO
import com.example.travelapp.domain.model.Booking
import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.model.RoomType
import com.example.travelapp.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor() : PropertyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val propertiesCollection = db.collection("Properties")
    private val bookingsCollection = db.collection("Bookings")

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

    override suspend fun saveProperty(property: Property): String? {
        return try {
            val dto = property.toDTO()
            val docRef = if (dto.proId.isEmpty()) {
                propertiesCollection.document()
            } else {
                propertiesCollection.document(dto.proId)
            }
            val finalDto = dto.copy(proId = docRef.id)
            docRef.set(finalDto).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteProperty(proId: String): Boolean {
        return try {
            propertiesCollection.document(proId).delete().await()
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

    override suspend fun deleteRoomType(proId: String, roomTypeId: String): Boolean {
        return try {
            propertiesCollection.document(proId).collection("RoomTypes").document(roomTypeId).delete().await()
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

    override suspend fun uploadPropertyImage(path: String, uri: Uri): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun checkRoomAvailability(proId: String, roomTypeId: String, startDate: Long, endDate: Long): Int {
        return try {
            val roomDoc = propertiesCollection.document(proId)
                .collection("RoomTypes").document(roomTypeId).get().await()
            val total = roomDoc.getLong("totalRooms")?.toInt() ?: 0

            val bookingsSnap = bookingsCollection
                .whereEqualTo("proId", proId)
                .whereEqualTo("hotelBooking.roomTypeId", roomTypeId)
                .whereIn("status", listOf("confirmed", "pending"))
                .get().await()

            val occupiedRooms = bookingsSnap.documents.filter { doc ->
                val bStart = doc.getLong("startDate") ?: 0L
                val bEnd = doc.getLong("endDate") ?: 0L
                startDate < bEnd && endDate > bStart
            }.sumOf { (it.get("hotelBooking.quantity") as? Long)?.toInt() ?: 1 }

            (total - occupiedRooms).coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun createBooking(booking: Booking): Boolean {
        return try {
            val docRef = bookingsCollection.document()
            val finalBooking = booking.copy(bookId = docRef.id)
            docRef.set(finalBooking).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePropertyStatus(proId: String, status: String): Boolean {
        return try {
            propertiesCollection.document(proId).update("status", status).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
