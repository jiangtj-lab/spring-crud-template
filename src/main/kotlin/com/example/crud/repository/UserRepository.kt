package com.example.crud.repository

import com.example.crud.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun existsByName(name: String): Boolean
    fun existsByEmail(email: String): Boolean
}
