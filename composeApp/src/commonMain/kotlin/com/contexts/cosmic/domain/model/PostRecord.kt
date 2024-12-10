package com.contexts.cosmic.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Record(
    @SerialName("\$type")
    val type: String,
    val text: String,
    val createdAt: String,
    val langs: List<String>? = null,
    val embed: Embed? = null,
    val reply: Reply? = null,
)
