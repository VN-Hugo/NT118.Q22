package com.example.travelapp.data.repository

import com.example.travelapp.data.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    suspend fun savePlan(plan: Plan): String?
    fun getUserPlans(userId: String): Flow<List<Plan>>
    suspend fun deletePlan(planId: String): Boolean
}
