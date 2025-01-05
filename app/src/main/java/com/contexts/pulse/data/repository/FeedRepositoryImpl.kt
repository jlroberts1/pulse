/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetListFeedResponse
import app.bsky.feed.GetSuggestedFeedsResponse
import app.bsky.feed.GetTimelineResponse
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import io.ktor.client.HttpClient
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FeedRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val client: HttpClient,
    private val feedAPI: FeedAPI,
    private val feedDao: FeedDao,
) : FeedRepository {
    override suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError> =
        withContext(appDispatchers.io) {
            feedAPI.getFeed(feedUri = feedUri, cursor = cursor)
        }

    override suspend fun getTimeline(cursor: String?): Response<GetTimelineResponse, NetworkError> =
        withContext(appDispatchers.io) {
            feedAPI.getTimeline(cursor = cursor)
        }

    override suspend fun getListFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetListFeedResponse, NetworkError> =
        withContext(appDispatchers.io) {
            feedAPI.getListFeed(feedUri = feedUri, cursor = cursor)
        }

    override fun getSuggestions(): Flow<PagingData<GeneratorView>> {
        val request = PagedRequest.SuggestedFeed()
        return Pager(
            config =
                PagingConfig(
                    pageSize = request.limit,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                NetworkPagingSource<GeneratorView, GetSuggestedFeedsResponse>(
                    appDispatchers = appDispatchers,
                    client = client,
                    request = request,
                    typeInfo<GetSuggestedFeedsResponse>(),
                    getItems = { it.feeds },
                    getCursor = { it.cursor },
                )
            },
        ).flow.flowOn(appDispatchers.io)
    }

    override fun getAvailableFeeds(did: String): Flow<List<FeedEntity>> {
        return feedDao.getFeedsForUser(did).flowOn(appDispatchers.io)
    }

    override suspend fun likePost(
        postUri: String,
        likeUri: String,
    ) = withContext(appDispatchers.io) {
        feedDao.likePost(postUri, likeUri)
    }

    override suspend fun unlikePost(postUri: String) =
        withContext(appDispatchers.io) {
            feedDao.unlikePost(postUri)
        }
}
