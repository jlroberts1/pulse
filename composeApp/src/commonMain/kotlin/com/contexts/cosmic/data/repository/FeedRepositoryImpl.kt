/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import app.bsky.feed.GetTimelineResponse
import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.exceptions.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FeedRepositoryImpl(private val feedAPI: FeedAPI) : FeedRepository {
    override suspend fun getTimeline(): Response<GetTimelineResponse, NetworkError> =
        withContext(Dispatchers.IO) {
            feedAPI.getTimeline()
        }
}
