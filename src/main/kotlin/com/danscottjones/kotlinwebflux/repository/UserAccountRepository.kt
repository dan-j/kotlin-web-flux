package com.danscottjones.kotlinwebflux.repository

import com.danscottjones.kotlinwebflux.model.UserAccount
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

interface UserAccountRepository : ReactiveCrudRepository<UserAccount, String>,
        ReactiveUserDetailsService {

    @Query("{ email: ?0 }")
    override fun findByUsername(username: String?): Mono<UserDetails>
}