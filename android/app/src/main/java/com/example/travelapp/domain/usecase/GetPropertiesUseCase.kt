package com.example.travelapp.domain.usecase

import com.example.travelapp.domain.model.Property
import com.example.travelapp.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPropertiesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(type: String? = null): Flow<List<Property>> {
        return repository.getProperties(type)
    }
}