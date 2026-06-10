package com.example.crud.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Request to create a new user")
data class CreateUserRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "User's name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "User's email", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,

    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    @Schema(description = "User's description", example = "A software developer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val description: String? = null
)

@Schema(description = "Request to update an existing user")
data class UpdateUserRequest(
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "User's name", example = "John Doe Updated")
    val name: String? = null,

    @field:Email(message = "Email must be valid")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "User's email", example = "john.updated@example.com")
    val email: String? = null,

    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    @Schema(description = "User's description", example = "Senior software developer")
    val description: String? = null
)

@Schema(description = "User response")
data class UserResponse(
    @Schema(description = "User ID", example = "1")
    val id: Long,

    @Schema(description = "User's name", example = "John Doe")
    val name: String,

    @Schema(description = "User's email", example = "john@example.com")
    val email: String,

    @Schema(description = "User's description", example = "A software developer")
    val description: String?,

    @Schema(description = "Creation timestamp")
    val createdAt: String,

    @Schema(description = "Last update timestamp")
    val updatedAt: String
)
