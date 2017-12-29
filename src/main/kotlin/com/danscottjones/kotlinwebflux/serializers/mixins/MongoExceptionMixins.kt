package com.danscottjones.kotlinwebflux.serializers.mixins

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mongodb.MongoServerException
import com.mongodb.ServerAddress

abstract class MongoServerExceptionMixin: MongoServerException("", ServerAddress("")) {

    @JsonIgnore
    override abstract fun getServerAddress(): ServerAddress

}

interface MongoExceptionMixin {

    @JsonIgnore
    fun getSuppressed(): List<Throwable>
}

