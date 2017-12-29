package com.danscottjones.kotlinwebflux.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
open class APIException(
        val code: Int = ErrorCodes.UNKNOWN,
        message: String? = null,
        cause: Throwable? = null): RuntimeException(message, cause)