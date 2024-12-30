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
import app.bsky.actor.PreferencesUnion
import app.bsky.actor.Type
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetSuggestedFeedsResponse
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.toPost
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
    private val profileAPI: ProfileAPI,
) : FeedRepository {
    override suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError> =
        withContext(appDispatchers.io) {
            feedAPI.getFeed(feedUri = feedUri, cursor = cursor)
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

    override fun getFeed(feedUri: String): Flow<PagingData<TimelinePost>> {
        val request = PagedRequest.GetFeed(feedUri)
        return Pager(
            config =
                PagingConfig(
                    pageSize = request.limit,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                NetworkPagingSource<TimelinePost, GetFeedResponse>(
                    appDispatchers = appDispatchers,
                    client = client,
                    request = request,
                    typeInfo<GetFeedResponse>(),
                    getItems = { it.feed.map { it.toPost() } },
                    getCursor = { it.cursor },
                )
            },
        ).flow.flowOn(appDispatchers.io)
    }

    override suspend fun refreshFeeds(did: String): Unit =
        withContext(appDispatchers.io) {
            profileAPI.getPreferences().onSuccess { response ->
                val savedFeeds =
                    response.preferences
                        .filterIsInstance<PreferencesUnion.SavedFeedsPrefV2>()
                        .map { it.value.items.filter { item -> item.type is Type.Feed } }
                        .firstOrNull()

                savedFeeds?.forEach { savedFeed ->
                    feedAPI.getFeedGenerator(savedFeed.value).onSuccess { response ->
                        val feed =
                            FeedEntity(
                                id = savedFeed.id,
                                userDid = did,
                                type = savedFeed.type.value,
                                uri = savedFeed.value,
                                pinned = savedFeed.pinned,
                                displayName = response.view.displayName,
                            )
                        feedDao.insertFeed(feed)
                    }
                }
            }
        }

    override fun getAvailableFeeds(did: String): Flow<List<FeedEntity>> {
        return feedDao.getFeedsForUser(did).flowOn(appDispatchers.io)
    }
}
