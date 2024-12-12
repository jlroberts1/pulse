/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.api

import app.bsky.feed.GetTimelineResponse
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.safeRequest
import com.contexts.cosmic.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class FeedAPI(private val client: HttpClient) {
    suspend fun getTimeline(
        limit: Int = 50,
        cursor: String? = null,
        algorithm: String? = null,
    ): Response<GetTimelineResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getTimeline")
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
                algorithm?.let { parameters.append("algorithm", it) }
            }
        }
    }
}
