package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ReplyReference(
    val cid: String? = null,
    val uri: String? = null,
)
