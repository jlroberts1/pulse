/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.feed.GetFeedGeneratorResponse
import app.bsky.feed.GetFeedResponse
import com.contexts.pulse.data.network.client.AccountManager
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class FeedAPI(
    client: HttpClient,
    accountManager: AccountManager,
) : BaseAPI(client, accountManager) {
    suspend fun getFeed(
        feedUri: String,
        limit: Int = 20,
        cursor: String? = null,
    ): Response<GetFeedResponse, NetworkError> {
        return client.safeRequest {
            configurePds()
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getFeed")
                parameters.append("feed", feedUri)
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getFeedGenerator(at: String): Response<GetFeedGeneratorResponse, NetworkError> {
        return client.safeRequest<GetFeedGeneratorResponse> {
            configurePds()
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getFeedGenerator")
                parameters.append("feed", at)
            }
        }
    }
}
