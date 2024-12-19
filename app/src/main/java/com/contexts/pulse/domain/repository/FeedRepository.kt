/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetSuggestedFeedsQueryParams
import app.bsky.feed.GetSuggestedFeedsResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.exceptions.NetworkError

interface FeedRepository {
    suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError>

    suspend fun getSuggestions(
        getFeedSuggestionsQueryParams: GetSuggestedFeedsQueryParams,
    ): Response<GetSuggestedFeedsResponse, NetworkError>
}
