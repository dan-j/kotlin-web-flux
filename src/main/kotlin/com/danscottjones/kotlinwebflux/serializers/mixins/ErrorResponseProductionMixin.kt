package com.danscottjones.kotlinwebflux.serializers.mixins

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class ErrorResponseProductionMixin {
    @JsonIgnore
    val exception: Throwable? = null
}