package com.contexts.cosmic.data.network.model.response

import com.contexts.cosmic.domain.model.Label
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val did: String,
    val handle: String,
    val displayName: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val followersCount: Int = 0,
    val followsCount: Int = 0,
    val postsCount: Int = 0,
    val indexedAt: String? = null,
    val labels: List<Label>? = null,
)
