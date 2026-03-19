package com.example.bookinglite.user.repository


import com.example.bookinglite.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {

    // 1. Tìm user theo username (Spring tự hiểu logic từ tên hàm)
    fun findByUsername(username: String): Optional<UserEntity>

    // 2. Tìm user theo email (Rất cần cho chức năng Login)
    fun findByEmail(email: String): Optional<UserEntity>

    // 3. Kiểm tra xem email đã tồn tại chưa (Dùng cho chức năng Đăng ký)
    fun existsByEmail(email: String): Boolean

    // 4. Tìm danh sách user theo Role (Ví dụ: lấy tất cả ADMIN)
    fun findAllByRole(role: String): List<UserEntity>
}