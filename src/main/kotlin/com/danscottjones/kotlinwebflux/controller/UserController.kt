package com.danscottjones.kotlinwebflux.controller

import com.danscottjones.kotlinwebflux.model.UserAccount
import com.danscottjones.kotlinwebflux.repository.UserAccountRepository
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import views.UserView

@RestController
@RequestMapping("/users")
class UserController(
        val userAccountRepository: UserAccountRepository,
        val passwordEncoder: BCryptPasswordEncoder
) {

    @JsonView(UserView.PublicView::class)
    @PostMapping
    fun create(@RequestBody userAccount: UserAccount,
               exchange: ServerWebExchange,
               uriBuilder: UriComponentsBuilder)
            : Mono<UserAccount> {
        return userAccountRepository
                .save(UserAccount(null,
                        userAccount.username,
                        passwordEncoder.encode(userAccount.password))).map {
            val response = exchange.response
            response.statusCode = HttpStatus.CREATED
            response.headers.location = uriBuilder.path("/${it.id}").build().toUri()
            return@map it
        }
    }

    @JsonView(UserView.AdminView::class)
    @GetMapping
    fun retrieveAll(): Flux<UserAccount> = userAccountRepository.findAll()

    @JsonView(UserView.PublicView::class)
    @GetMapping("/{id}")
    fun retrieve(@PathVariable id: String): Mono<UserAccount> = userAccountRepository.findById(id)

}