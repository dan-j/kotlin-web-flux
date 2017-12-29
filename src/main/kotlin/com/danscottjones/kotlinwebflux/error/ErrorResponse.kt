package com.danscottjones.kotlinwebflux.error

import com.danscottjones.kotlinwebflux.serializers.ThrowableSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class ErrorResponse(
        val code: Int = ErrorCodes.UNKNOWN,
        val message: String?,
        @JsonSerialize(using = ThrowableSerializer::class)
        val exception: Throwable?
)

fun APIException.toErrorResponse() = ErrorResponse(this.code, this.message, this)
fun Throwable.toErrorResponse() = ErrorResponse(message = this.message, exception = this)