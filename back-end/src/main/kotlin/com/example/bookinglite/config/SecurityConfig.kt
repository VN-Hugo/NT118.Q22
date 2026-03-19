package com.example.bookinglite.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.Customizer

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // TẮT CSRF để Postman có thể POST/PUT/DELETE
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Cho phép tất cả (tạm thời để test cho dễ)
            }
            .httpBasic(Customizer.withDefaults()) // Vẫn giữ Basic Auth nếu bạn muốn dùng

        return http.build()
    }
}