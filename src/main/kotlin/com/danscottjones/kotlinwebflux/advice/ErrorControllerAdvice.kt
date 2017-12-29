package com.danscottjones.kotlinwebflux.advice

import com.danscottjones.kotlinwebflux.error.APIException
import com.danscottjones.kotlinwebflux.error.ErrorCodes
import com.danscottjones.kotlinwebflux.error.ErrorResponse
import com.danscottjones.kotlinwebflux.error.toErrorResponse
import com.mongodb.MongoWriteException
import org.springframework.core.annotation.AnnotationUtils.findAnnotation
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorControllerAdvice {

    @ExceptionHandler(APIException::class)
    fun apiExceptionHandler(e: APIException): ResponseEntity<ErrorResponse> {
        val responseStatus: ResponseStatus? =
                findAnnotation(e::class.java, ResponseStatus::class.java)
        val status = responseStatus?.code ?: INTERNAL_SERVER_ERROR
        return ResponseEntity.status(status).body(e.toErrorResponse())
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun duplicateKeyExceptionHandler(e: DuplicateKeyException): ResponseEntity<ErrorResponse> {
        val message = (e.cause as? MongoWriteException)?.error?.message ?: e.message
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse(ErrorCodes.DUPLICATE_KEY, message, e))
    }

    @ExceptionHandler(Throwable::class)
    fun anyExceptionHandler(e: Throwable): ResponseEntity<ErrorResponse> = ResponseEntity
            .status(INTERNAL_SERVER_ERROR)
            .body(e.toErrorResponse())
}