package com.danscottjones.kotlinwebflux

import com.danscottjones.kotlinwebflux.model.UserAccount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerTests {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun testCreateUser() {
        webTestClient.post()
                .uri("/users")
                .body(BodyInserters.fromObject(UserAccount(null, "user@example.com", "")))
                .exchange()
                .expectStatus().isCreated
    }
}