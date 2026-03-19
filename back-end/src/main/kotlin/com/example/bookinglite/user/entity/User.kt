package com.example.bookinglite.user.entity

import jakarta.persistence.*
import java.time.Instant
import org.hibernate.annotations.CreationTimestamp
import java.util.UUID

@Entity
@Table(name = "users") // Tên bảng trong Supabase
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val userId: UUID? = null,

    @Column(nullable = false, unique = true)
    var username: String = "",
    var role: String = "USER",
    @Column(nullable = false, unique = true)
    var email: String = "",
    var avatarUrl: String? = null,
    var phone: String? = null, // Dùng String cho số điện thoại để tránh mất số 0 ở đầu
    @Column(nullable = false)
    var passwordHash: String = "",
    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: Instant = Instant.now()
)