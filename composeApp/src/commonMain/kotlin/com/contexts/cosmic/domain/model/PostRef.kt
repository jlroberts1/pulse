package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PostRef(
    val uri: String,
    val cid: String
)