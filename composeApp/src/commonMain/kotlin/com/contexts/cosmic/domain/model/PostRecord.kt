package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PostRecord(
    val text: String,
    val createdAt: String,
    val embed: Embed? = null
)