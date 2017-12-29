package com.danscottjones.kotlinwebflux.config

import com.danscottjones.kotlinwebflux.error.ErrorResponse
import com.danscottjones.kotlinwebflux.serializers.mixins.ErrorResponseProductionMixin
import com.danscottjones.kotlinwebflux.serializers.mixins.MongoExceptionMixin
import com.danscottjones.kotlinwebflux.serializers.mixins.MongoServerExceptionMixin
import com.danscottjones.kotlinwebflux.serializers.mixins.ThrowableMixin
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.mongodb.MongoException
import com.mongodb.MongoServerException
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder.featuresToDisable(MapperFeature.DEFAULT_VIEW_INCLUSION)

        builder.serializerByType(StackTraceElement::class.java, ToStringSerializer())

        builder.mixIn(Throwable::class.java, ThrowableMixin::class.java)
        builder.mixIn(MongoServerException::class.java, MongoServerExceptionMixin::class.java)
        builder.mixIn(MongoException::class.java, MongoExceptionMixin::class.java)
    }

    @Bean
    @Profile("production")
    fun errorResponseProductionMixin() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder.mixIn(ErrorResponse::class.java, ErrorResponseProductionMixin::class.java)
    }

    @Bean
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper = builder.build()

}