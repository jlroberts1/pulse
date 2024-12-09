package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ReplyRef(
    val root: PostRef? = null,
    val parent: PostRef? = null,
)
