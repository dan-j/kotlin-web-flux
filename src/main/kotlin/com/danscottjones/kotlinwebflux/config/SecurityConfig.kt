package com.danscottjones.kotlinwebflux.config

import com.danscottjones.kotlinwebflux.model.UserAccount
import com.danscottjones.kotlinwebflux.repository.UserAccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAuthority
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.reactive.result.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(userAccountRepository: UserAccountRepository,
                              passwordEncoder: BCryptPasswordEncoder)
            : ReactiveAuthenticationManager {
        val userDetailsRepositoryAuthManager =
                UserDetailsRepositoryReactiveAuthenticationManager(userAccountRepository)
        userDetailsRepositoryAuthManager.setPasswordEncoder(passwordEncoder)
        return userDetailsRepositoryAuthManager
    }

    @Bean
    fun springSecurityFilterChain(userAccountRepository: UserAccountRepository,
                                  passwordEncoder: BCryptPasswordEncoder)
            : SecurityWebFilterChain {
        val http = http()
        return http.authenticationManager(
                authenticationManager(userAccountRepository, passwordEncoder))
                .httpBasic()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/users").permitAll()
                .pathMatchers(HttpMethod.GET, "/users/{id}").access(this::currentUserOrAdmin)
                .anyExchange().hasRole("ADMIN")
                .and()
                .csrf().disable()
                .build()
    }

    private fun currentUserOrAdmin(authentication: Mono<Authentication>,
                                   context: AuthorizationContext)
            : Mono<AuthorizationDecision> {
        return authentication
                .filter { auth -> context.variables["id"] == (auth.principal as? UserAccount)?.id }
                .map { AuthorizationDecision(true) }
                .switchIfEmpty(
                        hasAuthority<AuthorizationContext>("ROLE_ADMIN")
                                .check(authentication, context)
                )
    }

    @Bean
    fun authenticationPrincipalArgumentResolver(): AuthenticationPrincipalArgumentResolver {
        return AuthenticationPrincipalArgumentResolver(ReactiveAdapterRegistry())
    }

}