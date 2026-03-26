package com.example.travelapp.domain.usecase


import com.example.travelapp.domain.model.User
import com.example.travelapp.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(email: String, pass: String, name: String): Boolean {
        val uid = repository.registerUser(email, pass) ?: return false

        val user = User(
            uid = uid,
            email = email,
            fullName = name
        )

        return repository.saveUser(user)
    }
}