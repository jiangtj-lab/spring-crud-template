package com.example.crud.exception

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.util.HashMap

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val timestamp: String = LocalDateTime.now().toString(),
        val status: Int,
        val error: String,
        val message: String,
        val path: String? = null
    )

    @ExceptionHandler(ResourceNotFoundException::class)
    @ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Resource not found"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(ResourceConflictException::class)
    @ApiResponse(
        responseCode = "409",
        description = "Resource conflict",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleResourceConflict(ex: ResourceConflictException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = ex.message ?: "Resource conflict"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = HashMap<String, Any>()
        errors["timestamp"] = LocalDateTime.now().toString()
        errors["status"] = HttpStatus.BAD_REQUEST.value()
        errors["error"] = HttpStatus.BAD_REQUEST.reasonPhrase
        errors["message"] = "Validation failed"

        val fieldErrors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        errors["errors"] = fieldErrors

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    @ExceptionHandler(Exception::class)
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))]
    )
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = ex.message ?: "Internal server error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
