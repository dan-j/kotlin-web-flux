package com.danscottjones.kotlinwebflux.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadRequestException(
        code: Int = ErrorCodes.UNKNOWN,
        message: String? = null,
        cause: Throwable? = null)
    : APIException(code, message, cause)
