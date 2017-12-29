package com.danscottjones.kotlinwebflux

import com.danscottjones.kotlinwebflux.model.UserAccount
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions

@ExtendWith(SpringExtension::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class SecurityTests {

    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var context: ApplicationContext

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun beforeEach() {
        webTestClient = WebTestClientHelper.getSecurityMockWebTestClientBuilder(context)
                .baseUrl("/users")
                .build()
    }

    @Test
    @WithMockUser(roles = arrayOf("ADMIN"))
    fun adminCanGetUsers() {
        webTestClient
                .get()
                .exchange()
                .expectStatus().isOk
                .expectBody().jsonPath("$").isArray
    }

    @Test
    fun unauthorizedUsersCannotGetUsers() {
        webTestClient
                .get()
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun authorizedUserCanAccessOwnDetails() {
        val user = UserAccount(null, "user", BCryptPasswordEncoder().encode("123"))
        mongoTemplate.insert(user)

        assertThat(user.id).isNotNull().withFailMessage("Unable to persist initial user")

        webTestClient
                .mutateWith { builder, _, _ ->
                    builder.filter(ExchangeFilterFunctions.basicAuthentication("user", "123"))
                }
                .get()
                .uri("/${user.id}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(user.id!!)
                .jsonPath("$.email").isEqualTo(user.email)
    }

    @Test
    fun authorizedUserCannotAccessOtherDetails() {
        val user1 = UserAccount(null, "user1", BCryptPasswordEncoder().encode("123"))
        val user2 = UserAccount(null, "user2", BCryptPasswordEncoder().encode("123"))
        mongoTemplate.insert(user1)
        mongoTemplate.insert(user2)

        assertThat(user1.id).isNotNull().withFailMessage("Unable to persist initial user1")
        assertThat(user2.id).isNotNull().withFailMessage("Unable to persist initial user2")

        webTestClient
                .mutateWith { builder, _, _ ->
                    builder.filter(ExchangeFilterFunctions.basicAuthentication("user2", "123"))
                }
                .get()
                .uri("/${user1.id}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody().isEmpty
    }

    @Test
    fun anyoneCanCreateUser() {
        val email = "user@example.com"
        webTestClient
                .post()
                .syncBody(UserAccount(null, email, "password"))
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .jsonPath("$.id").isNotEmpty
                .jsonPath("$.email").isEqualTo(email)
                .consumeWith { result ->
                    val location = result.responseHeaders.location?.path
                    val id: String = JsonPath.read(result.responseBody?.inputStream(), "$.id")
                    assertThat(location).isEqualTo("${result.url.path}/$id")
                    assertThat(mongoTemplate.findById(id, UserAccount::class.java)).isNotNull()
                }

    }
}