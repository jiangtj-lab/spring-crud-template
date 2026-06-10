package com.example.crud.controller

import com.example.crud.dto.CreateUserRequest
import com.example.crud.dto.UpdateUserRequest
import com.example.crud.dto.UserResponse
import com.example.crud.exception.ResourceNotFoundException
import com.example.crud.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(UserController::class)
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var userService: UserService

    private lateinit var testUserResponse: UserResponse

    @BeforeEach
    fun setUp() {
        testUserResponse = UserResponse(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            description = "A developer",
            createdAt = "2024-01-01T10:00:00",
            updatedAt = "2024-01-01T10:00:00"
        )
    }

    @Nested
    @DisplayName("GET /api/users")
    inner class GetAllUsers {

        @Test
        @DisplayName("should return all users")
        fun shouldReturnAllUsers() {
            val users = listOf(testUserResponse)
            org.mockito.Mockito.`when`(userService.getAllUsers()).thenReturn(users)

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].name").value("John Doe"))
        }

        @Test
        @DisplayName("should return empty list when no users")
        fun shouldReturnEmptyList() {
            org.mockito.Mockito.`when`(userService.getAllUsers()).thenReturn(emptyList())

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isEmpty)
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    inner class GetUserById {

        @Test
        @DisplayName("should return user when found")
        fun shouldReturnUserWhenFound() {
            org.mockito.Mockito.`when`(userService.getUserById(1L)).thenReturn(testUserResponse)

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.email").value("john@example.com"))
        }

        @Test
        @DisplayName("should return 404 when not found")
        fun shouldReturn404WhenNotFound() {
            org.mockito.Mockito.`when`(userService.getUserById(99L)).thenThrow(ResourceNotFoundException("User not found with id: 99"))

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users/99"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    inner class CreateUser {

        @Test
        @DisplayName("should create user successfully")
        fun shouldCreateUserSuccessfully() {
            val request = CreateUserRequest("John Doe", "john@example.com", "A developer")
            org.mockito.Mockito.`when`(userService.createUser(org.mockito.ArgumentMatchers.refEq(request)))
                .thenReturn(testUserResponse)

            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        fun shouldReturn400WhenNameIsBlank() {
            val request = CreateUserRequest("", "john@example.com", "A developer")

            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest)
        }

        @Test
        @DisplayName("should return 400 when email is invalid")
        fun shouldReturn400WhenEmailIsInvalid() {
            val request = CreateUserRequest("John Doe", "invalid-email", "A developer")

            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    inner class UpdateUser {

        @Test
        @DisplayName("should update user successfully")
        fun shouldUpdateUserSuccessfully() {
            val request = UpdateUserRequest(name = "Jane Doe")
            val updatedResponse = testUserResponse.copy(name = "Jane Doe")
            org.mockito.Mockito.`when`(userService.updateUser(org.mockito.Mockito.eq(1L), org.mockito.ArgumentMatchers.refEq(request)))
                .thenReturn(updatedResponse)

            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    inner class DeleteUser {

        @Test
        @DisplayName("should delete user successfully")
        fun shouldDeleteUserSuccessfully() {
            org.mockito.Mockito.doNothing().`when`(userService).deleteUser(1L)

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent)
        }

        @Test
        @DisplayName("should return 404 when not found")
        fun shouldReturn404WhenNotFound() {
            org.mockito.Mockito.doThrow(ResourceNotFoundException("User not found with id: 99")).`when`(userService).deleteUser(99L)

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/users/99"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound)
        }
    }
}
