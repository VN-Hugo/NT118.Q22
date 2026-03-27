package com.example.travelapp.domain.usecase

import com.example.travelapp.domain.repository.UserRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(idToken: String): Boolean {
        return repository.signInWithGoogle(idToken)
    }
}