package com.example.bookinglite.user.controller

import com.example.bookinglite.user.dto.UserDTO
import com.example.bookinglite.user.entity.UserEntity
import com.example.bookinglite.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import com.example.bookinglite.user.service.toEntity
@RestController
@RequestMapping("/api/users") // Đây là đường dẫn bạn sẽ gọi trong Postman
class UserController(
    private val userService: UserService
) {

    // 1. Lấy toàn bộ danh sách User
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDTO>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    // 2. Lấy 1 User theo ID (UUID)
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserDTO> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 3. Tạo User mới (Dùng để test POST lên Supabase)
    @PostMapping
    fun createUser(@RequestBody dto: UserDTO): ResponseEntity<UserDTO> {
        return try {
            // Dùng hàm Mapper .toEntity() bạn vừa viết lúc nãy
            val newUser = userService.createUser(dto.toEntity(passwordHash = "123456"))
            ResponseEntity.status(HttpStatus.CREATED).body(newUser)
        } catch (e: Exception) {
            // In lỗi ra Console để bạn dễ debug
            println("Lỗi tạo user: ${e.message}")
            ResponseEntity.badRequest().build()
        }
    }

    // 4. Xóa User
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Unit> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}