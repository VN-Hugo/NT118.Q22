package com.example.travelapp.data.repository

import com.example.travelapp.data.mapper.toDomain
import com.example.travelapp.data.remote.dto.PropertyDTO
import com.example.travelapp.domain.model.Property
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
}