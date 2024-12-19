/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import chat.bsky.convo.ListConvosQueryParams
import chat.bsky.convo.ListConvosResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.path

class ChatAPI(private val client: HttpClient) {
    suspend fun listConvos(
        pdsUrl: String?,
        listConvosQueryParams: ListConvosQueryParams,
    ): Response<ListConvosResponse, NetworkError> {
        return client.safeRequest {
            header("atproto-proxy", "did:web:api.bsky.chat#bsky_chat")
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl ?: "").host
                listConvosQueryParams.cursor?.let { parameters.append("cursor", it) }
                listConvosQueryParams.limit?.let { parameters.append("limit", it.toString()) }
                path("xrpc/chat.bsky.convo.listConvos")
            }
        }
    }
}
