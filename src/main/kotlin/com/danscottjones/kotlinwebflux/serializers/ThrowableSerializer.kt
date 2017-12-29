package com.danscottjones.kotlinwebflux.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class ThrowableSerializer : StdSerializer<Throwable>(Throwable::class.java) {

    override fun serialize(value: Throwable?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (gen == null) {
            throw IllegalArgumentException("gen cannot be null")
        }
        if (provider == null) {
            throw IllegalArgumentException("provider cannot be null")
        }

        value?.let {
            gen.writeStartObject(value)
            provider.defaultSerializeField("message", value.message, gen)
            provider.defaultSerializeField("stackTrace", value.stackTrace, gen)
            value.cause?.let { cause ->
                gen.writeFieldName("cause")
                this.serialize(cause, gen, provider)
            }
            gen.writeEndObject()
        }
    }
}