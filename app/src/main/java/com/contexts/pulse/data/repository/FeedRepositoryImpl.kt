/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetSuggestedFeedsQueryParams
import app.bsky.feed.GetSuggestedFeedsResponse
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.exceptions.NetworkError

class FeedRepositoryImpl(private val feedAPI: FeedAPI) : FeedRepository {
    override suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError> = feedAPI.getFeed(feedUri = feedUri, cursor = cursor)

    override suspend fun getSuggestions(
        getFeedSuggestionsQueryParams: GetSuggestedFeedsQueryParams,
    ): Response<GetSuggestedFeedsResponse, NetworkError> {
        return feedAPI.getSuggestions(getFeedSuggestionsQueryParams)
    }
}
