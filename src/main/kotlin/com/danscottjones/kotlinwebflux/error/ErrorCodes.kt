package com.danscottjones.kotlinwebflux.error

class ErrorCodes private constructor() {
    companion object {
        val UNKNOWN = 99999
        val DUPLICATE_KEY = 11000
    }
}