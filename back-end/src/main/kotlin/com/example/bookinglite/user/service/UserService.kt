package com.example.bookinglite.user.service

import com.example.bookinglite.user.dto.UserDTO
import com.example.bookinglite.user.entity.UserEntity
import com.example.bookinglite.user.repository.UserRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    // 1. Lấy tất cả người dùng và chuyển sang DTO
    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserDTO> {
        return userRepository.findAll().map { it.toDTO() }
    }

    // 2. Tìm một người dùng theo UUID
    @Transactional(readOnly = true)
    fun getUserById(id: UUID): UserDTO? {
        return userRepository.findById(id).map { it.toDTO() }.orElse(null)
    }

    // 3. Tạo người dùng mới
    @Transactional
    fun createUser(entity: UserEntity): UserDTO {
        // Kiểm tra email trùng trước khi lưu
        if (userRepository.existsByEmail(entity.email)) {
            throw RuntimeException("Email này đã tồn tại rồi!")
        }

        // Lưu vào Supabase thông qua Repository
        val savedUser = userRepository.save(entity)
        // Trả về DTO để phản hồi cho Postman
        return savedUser.toDTO()
    }

    // 4. Xóa người dùng
    @Transactional
    fun deleteUser(id: UUID) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
        }
    }
}