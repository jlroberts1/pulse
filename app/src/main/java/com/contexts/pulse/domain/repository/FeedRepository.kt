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
import app.bsky.feed.GetListFeedResponse
import app.bsky.feed.GetTimelineResponse
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.exceptions.NetworkError
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError>

    suspend fun getTimeline(cursor: String?): Response<GetTimelineResponse, NetworkError>

    suspend fun getListFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetListFeedResponse, NetworkError>

    fun getSuggestions(): Flow<PagingData<GeneratorView>>

    fun getAvailableFeeds(did: String): Flow<List<FeedEntity>>

    suspend fun likePost(
        postUri: String,
        likeUri: String,
    )

    suspend fun unlikePost(postUri: String)
}
