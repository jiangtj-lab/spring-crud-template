package com.example.crud.service

import com.example.crud.dto.CreateUserRequest
import com.example.crud.dto.UpdateUserRequest
import com.example.crud.dto.UserResponse
import com.example.crud.entity.User
import com.example.crud.exception.ResourceConflictException
import com.example.crud.exception.ResourceNotFoundException
import com.example.crud.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    private lateinit var testUser: User
    private lateinit var testUserResponse: UserResponse

    @BeforeEach
    fun setUp() {
        testUser = User(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            description = "A developer",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        testUserResponse = UserResponse(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            description = "A developer",
            createdAt = LocalDateTime.now().toString(),
            updatedAt = LocalDateTime.now().toString()
        )
    }

    @Nested
    @DisplayName("getAllUsers")
    inner class GetAllUsers {

        @Test
        @DisplayName("should return all users")
        fun shouldReturnAllUsers() {
            val users = listOf(testUser)
            org.mockito.Mockito.`when`(userRepository.findAll()).thenReturn(users)

            val result = userService.getAllUsers()

            org.junit.jupiter.api.Assertions.assertEquals(1, result.size)
            org.junit.jupiter.api.Assertions.assertEquals("John Doe", result[0].name)
        }

        @Test
        @DisplayName("should return empty list when no users")
        fun shouldReturnEmptyList() {
            org.mockito.Mockito.`when`(userRepository.findAll()).thenReturn(emptyList())

            val result = userService.getAllUsers()

            org.junit.jupiter.api.Assertions.assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("getUserById")
    inner class GetUserById {

        @Test
        @DisplayName("should return user when found")
        fun shouldReturnUserWhenFound() {
            org.mockito.Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))

            val result = userService.getUserById(1L)

            org.junit.jupiter.api.Assertions.assertEquals("John Doe", result.name)
            org.junit.jupiter.api.Assertions.assertEquals("john@example.com", result.email)
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        fun shouldThrowWhenNotFound() {
            org.mockito.Mockito.`when`(userRepository.findById(99L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
                userService.getUserById(99L)
            }
        }
    }

    @Nested
    @DisplayName("createUser")
    inner class CreateUser {

        @Test
        @DisplayName("should create user successfully")
        fun shouldCreateUserSuccessfully() {
            val request = CreateUserRequest("John Doe", "john@example.com", "A developer")
            org.mockito.Mockito.`when`(userRepository.existsByName(request.name)).thenReturn(false)
            org.mockito.Mockito.`when`(userRepository.existsByEmail(request.email)).thenReturn(false)
            org.mockito.Mockito.`when`(userRepository.save(org.mockito.ArgumentMatchers.any(User::class.java))).thenReturn(testUser)

            val result = userService.createUser(request)

            org.junit.jupiter.api.Assertions.assertEquals("John Doe", result.name)
            org.mockito.Mockito.verify(userRepository).save(org.mockito.ArgumentMatchers.any(User::class.java))
        }

        @Test
        @DisplayName("should throw ResourceConflictException when name exists")
        fun shouldThrowWhenNameExists() {
            val request = CreateUserRequest("John Doe", "john@example.com", "A developer")
            org.mockito.Mockito.`when`(userRepository.existsByName(request.name)).thenReturn(true)

            org.junit.jupiter.api.Assertions.assertThrows(ResourceConflictException::class.java) {
                userService.createUser(request)
            }
        }

        @Test
        @DisplayName("should throw ResourceConflictException when email exists")
        fun shouldThrowWhenEmailExists() {
            val request = CreateUserRequest("John Doe", "john@example.com", "A developer")
            org.mockito.Mockito.`when`(userRepository.existsByName(request.name)).thenReturn(false)
            org.mockito.Mockito.`when`(userRepository.existsByEmail(request.email)).thenReturn(true)

            org.junit.jupiter.api.Assertions.assertThrows(ResourceConflictException::class.java) {
                userService.createUser(request)
            }
        }
    }

    @Nested
    @DisplayName("updateUser")
    inner class UpdateUser {

        @Test
        @DisplayName("should update user successfully")
        fun shouldUpdateUserSuccessfully() {
            val request = UpdateUserRequest(name = "Jane Doe", email = "jane@example.com")
            org.mockito.Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))
            org.mockito.Mockito.`when`(userRepository.existsByName("Jane Doe")).thenReturn(false)
            org.mockito.Mockito.`when`(userRepository.existsByEmail("jane@example.com")).thenReturn(false)
            org.mockito.Mockito.`when`(userRepository.save(org.mockito.ArgumentMatchers.any(User::class.java))).thenReturn(testUser)

            val result = userService.updateUser(1L, request)

            org.junit.jupiter.api.Assertions.assertNotNull(result)
            org.mockito.Mockito.verify(userRepository).save(org.mockito.ArgumentMatchers.any(User::class.java))
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        fun shouldThrowWhenUserNotFound() {
            val request = UpdateUserRequest(name = "Jane Doe")
            org.mockito.Mockito.`when`(userRepository.findById(99L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
                userService.updateUser(99L, request)
            }
        }
    }

    @Nested
    @DisplayName("deleteUser")
    inner class DeleteUser {

        @Test
        @DisplayName("should delete user successfully")
        fun shouldDeleteUserSuccessfully() {
            org.mockito.Mockito.`when`(userRepository.existsById(1L)).thenReturn(true)
            org.mockito.Mockito.doNothing().`when`(userRepository).deleteById(1L)

            org.junit.jupiter.api.Assertions.assertDoesNotThrow {
                userService.deleteUser(1L)
            }
            org.mockito.Mockito.verify(userRepository).deleteById(1L)
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        fun shouldThrowWhenUserNotFound() {
            org.mockito.Mockito.`when`(userRepository.existsById(99L)).thenReturn(false)

            org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
                userService.deleteUser(99L)
            }
        }
    }
}
