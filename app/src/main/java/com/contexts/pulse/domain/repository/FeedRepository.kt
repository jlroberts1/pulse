/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import androidx.paging.PagingData
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedResponse
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.exceptions.NetworkError
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError>

    fun getSuggestions(): Flow<PagingData<GeneratorView>>

    fun getFeed(feedUri: String): Flow<PagingData<TimelinePost>>

    suspend fun refreshFeeds(did: String)

    fun getAvailableFeeds(did: String): Flow<List<FeedEntity>>
}
