package com.contexts.cosmic.data.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    val identifier: String,
    val password: String
)