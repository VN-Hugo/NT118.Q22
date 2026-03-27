package com.example.travelapp.domain.usecase

import com.example.travelapp.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, pass: String): Boolean {
        return repository.loginUser(email, pass)
    }
}