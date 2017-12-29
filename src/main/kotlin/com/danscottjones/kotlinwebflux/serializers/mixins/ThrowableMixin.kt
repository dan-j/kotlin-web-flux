package com.danscottjones.kotlinwebflux.serializers.mixins

abstract class ThrowableMixin : Throwable() {

    override var cause: Throwable? = null
        get() = super.cause

    override var message: String? = null
        get() = super.message

}