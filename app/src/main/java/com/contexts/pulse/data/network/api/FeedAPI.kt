/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetSuggestedFeedsQueryParams
import app.bsky.feed.GetSuggestedFeedsResponse
import app.bsky.feed.GetTimelineResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class FeedAPI(private val client: HttpClient) {
    suspend fun getTimeline(
        limit: Int = 50,
        cursor: String? = null,
    ): Response<GetTimelineResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getTimeline")
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getFeed(
        feedUri: String,
        limit: Int = 15,
        cursor: String? = null,
    ): Response<GetFeedResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getFeed")
                parameters.append("feed", feedUri)
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }

    suspend fun getSuggestions(getFeedSuggestionsParams: GetSuggestedFeedsQueryParams): Response<GetSuggestedFeedsResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getSuggestedFeeds")
                getFeedSuggestionsParams.limit?.let { parameters.append("limit", it.toString()) }
                getFeedSuggestionsParams.cursor?.let { parameters.append("cursor", it) }
            }
        }
    }
}
