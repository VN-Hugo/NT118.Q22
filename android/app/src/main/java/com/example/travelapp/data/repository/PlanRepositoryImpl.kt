package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Plan
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : PlanRepository {

    private val plansCollection = db.collection("Plans")

    override suspend fun savePlan(plan: Plan): String? {
        return try {
            val docRef = if (plan.planId.isEmpty()) {
                plansCollection.document()
            } else {
                plansCollection.document(plan.planId)
            }
            val finalPlan = plan.copy(planId = docRef.id)
            docRef.set(finalPlan).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    override fun getUserPlans(userId: String): Flow<List<Plan>> = callbackFlow {
        val subscription = plansCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects(Plan::class.java)) }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun deletePlan(planId: String): Boolean {
        return try {
            plansCollection.document(planId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
