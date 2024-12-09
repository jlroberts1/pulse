package com.contexts.cosmic.data.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RefreshSessionResponse(
    val accessJwt: String,
    val refreshJwt: String,
    val handle: String,
    val did: String,
)
