package com.danscottjones.kotlinwebflux.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer

@Component
class WebFluxConfig(val objectMapper: ObjectMapper): WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer?) {
        configurer?.let {
            val codecs = configurer.defaultCodecs() as? CodecConfigurer.DefaultCodecs
            codecs?.jackson2JsonEncoder(Jackson2JsonEncoder(this.objectMapper))
        }
    }
}