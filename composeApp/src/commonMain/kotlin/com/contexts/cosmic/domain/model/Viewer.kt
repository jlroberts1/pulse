package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Viewer(
    val repost: String? = null,
    val threadMuted: Boolean,
    val embeddingDisabled: Boolean,
)
