package com.contexts.cosmic.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val accessJwt: String,
    val refreshJwt: String,
)