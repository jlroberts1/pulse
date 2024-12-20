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
import app.bsky.feed.GetSuggestedFeedsResponse
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.NetworkPagingSource
import com.contexts.pulse.domain.repository.PagedRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class FeedRepositoryImpl(
    private val client: HttpClient,
    private val feedAPI: FeedAPI,
) : FeedRepository {
    override suspend fun getFeed(
        feedUri: String,
        cursor: String?,
    ): Response<GetFeedResponse, NetworkError> = feedAPI.getFeed(feedUri = feedUri, cursor = cursor)

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
                    client = client,
                    request = request,
                    typeInfo<GetSuggestedFeedsResponse>(),
                    getItems = { it.feeds },
                    getCursor = { it.cursor },
                )
            },
        ).flow.flowOn(Dispatchers.IO)
    }
}
