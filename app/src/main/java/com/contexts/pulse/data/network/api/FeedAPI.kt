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
import app.bsky.feed.GetListFeedResponse
import app.bsky.feed.GetTimelineResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.path

class FeedAPI(
    private val client: HttpClient,
) {
    suspend fun getFeed(
        feedUri: String,
        limit: Int = 20,
        cursor: String? = null,
    ): Response<GetFeedResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.feed.getFeed")
                parameters.append("feed", feedUri)
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getTimeline(
        limit: Int = 20,
        cursor: String? = null,
    ): Response<GetTimelineResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.feed.getTimeline")
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getListFeed(
        feedUri: String,
        limit: Int = 20,
        cursor: String? = null,
    ): Response<GetListFeedResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.feed.getListFeed")
                parameters.append("feed", feedUri)
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getFeedGenerator(at: String): Response<GetFeedGeneratorResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest<GetFeedGeneratorResponse> {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.feed.getFeedGenerator")
                parameters.append("feed", at)
            }
        }
    }
}
