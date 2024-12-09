package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedReason(
    val type: String,
)
