package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Reply(
    val parent: ReplyReference,
    val root: ReplyReference,
)
