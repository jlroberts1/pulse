package com.contexts.cosmic.domain.model

import com.contexts.cosmic.db.Auth_state

data class AuthState(
    val userDid: String,
    val accessJwt: String,
    val refreshJwt: String,
    val lastRefreshed: String,
)

fun Auth_state.toAuthState() =
    AuthState(
        userDid = this.userDid,
        accessJwt = this.accessJwt,
        refreshJwt = this.refreshJwt,
        lastRefreshed = this.lastRefreshed,
    )
