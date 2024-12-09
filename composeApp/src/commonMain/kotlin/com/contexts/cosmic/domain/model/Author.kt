package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val did: String,
    val handle: String,
    val displayName: String? = null,
    val avatar: String? = null,
)
