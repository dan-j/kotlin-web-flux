package com.danscottjones.kotlinwebflux.model

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import views.UserView

/**
 * Model representing a UserAccount. A kotlin data class can't be used because we wish to extend this class
 */
@Document(collection = "users")
open class UserAccount(
        @JsonView(UserView.PublicView::class) @Id val id: String? = null,

        @Indexed(unique = true)
        @JsonView(UserView.CreateView::class, UserView.PublicView::class)
        val email: String,

        @JsonView(UserView.CreateView::class)
        password: String?,

        roles: Set<String> = emptySet()
) : User(email, password, rolesToAuthority(roles)) {

    private constructor() : this(null, "", "")

    companion object {
        fun rolesToAuthority(roles: Set<String>) = roles.map { SimpleGrantedAuthority(it) }
        fun authorityToRoles(authorities: Collection<GrantedAuthority>) = authorities.map {
            it.authority
        }.toSet()
    }

    @JsonView(UserView.AdminView::class)
    private val roles: Set<String> = authorityToRoles(authorities)

    @Transient
    override fun getUsername(): String {
        return super.getUsername()
    }

    @Transient
    override fun getName(): String {
        return super.getName()
    }

    override fun toString() =
            "UserAccount(" +
                    "id=$id," +
                    "username=$username," +
                    "password=$password," +
                    "roles=${roles.joinToString(",")}" +
                    ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAccount

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}