/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import com.contexts.cosmic.db.Auth_state
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class AuthState(
    val userDid: String,
    val accessJwt: String,
    val refreshJwt: String,
    val lastRefreshed: String,
    val didDocument: DidDocument?,
)

@Serializable
data class DidDocument(
    @SerialName("@context")
    val context: List<String>,
    val id: String,
    val alsoKnownAs: List<String>,
    val verificationMethod: List<VerificationMethod>,
    val service: List<Service>,
)

@Serializable
data class VerificationMethod(
    val id: String,
    val type: String,
    val controller: String,
    val publicKeyMultibase: String,
)

@Serializable
data class Service(
    val id: String,
    val type: String,
    val serviceEndpoint: String,
)

fun Auth_state.toAuthState() =
    AuthState(
        userDid = this.userDid,
        accessJwt = this.accessJwt,
        refreshJwt = this.refreshJwt,
        lastRefreshed = this.lastRefreshed,
        didDocument = this.didDocument,
    )
