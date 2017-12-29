package com.danscottjones.kotlinwebflux.serializers.mixins

import org.springframework.core.NestedRuntimeException

abstract class NestedRuntimeExceptionMixin: NestedRuntimeException("") {
}