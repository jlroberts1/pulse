package com.contexts.cosmic.data.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionResponse(
    val accessJwt: String,
    val refreshJwt: String,
    val handle: String,
    val did: String,
    val email: String? = null
)