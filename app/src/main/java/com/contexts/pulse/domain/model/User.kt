/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import com.atproto.server.CreateSessionResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val did: String,
    val didDoc: DidDocument?,
    val handle: String,
    val email: String?,
    val accessJwt: String,
    val refreshJwt: String,
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

fun CreateSessionResponse.toUser() =
    User(
        did = did.did,
        didDoc = getDidDoc(),
        handle = handle.handle,
        email = email,
        accessJwt = accessJwt,
        refreshJwt = refreshJwt,
    )

fun CreateSessionResponse.getDidDoc() = this.didDoc?.decodeAs<DidDocument>()
