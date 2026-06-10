package com.example.crud.service

import com.example.crud.dto.CreateUserRequest
import com.example.crud.dto.UpdateUserRequest
import com.example.crud.dto.UserResponse
import com.example.crud.entity.User
import com.example.crud.exception.ResourceConflictException
import com.example.crud.exception.ResourceNotFoundException
import com.example.crud.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return user.toResponse()
    }

    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByName(request.name)) {
            throw ResourceConflictException("User already exists with name: ${request.name}")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw ResourceConflictException("User already exists with email: ${request.email}")
        }

        val user = User(
            name = request.name,
            email = request.email,
            description = request.description
        )
        return userRepository.save(user).toResponse()
    }

    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        request.name?.let {
            if (it != user.name && userRepository.existsByName(it)) {
                throw ResourceConflictException("User already exists with name: $it")
            }
            user.name = it
        }

        request.email?.let {
            if (it != user.email && userRepository.existsByEmail(it)) {
                throw ResourceConflictException("User already exists with email: $it")
            }
            user.email = it
        }

        request.description?.let {
            user.description = it
        }

        user.updatedAt = java.time.LocalDateTime.now()
        return userRepository.save(user).toResponse()
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }

    private fun User.toResponse() = UserResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        description = this.description,
        createdAt = this.createdAt.format(formatter),
        updatedAt = this.updatedAt.format(formatter)
    )
}
