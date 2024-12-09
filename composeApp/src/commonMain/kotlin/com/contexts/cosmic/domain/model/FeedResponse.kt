package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val cursor: String? = null,
    val feed: List<FeedViewPost>,
)
