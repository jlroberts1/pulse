/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.entities.toPost
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

class TimelineManager(
    private val appDispatchers: AppDispatchers,
    private val feedDao: FeedDao,
    private val timelineRemoteMediatorFactory: TimelineRemoteMediatorFactory,
) {
    private val feedMediators = ConcurrentHashMap<String, TimelineRemoteMediator>()

    private fun getOrCreateMediator(
        feedId: String,
        feedUri: String,
    ): TimelineRemoteMediator {
        return feedMediators.getOrPut(feedId) {
            timelineRemoteMediatorFactory.create(feedId, feedUri)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getFeedPagingFlow(
        feedId: String,
        feedUri: String,
    ): Flow<PagingData<TimelinePost>> {
        return Pager(
            config =
                PagingConfig(
                    pageSize = 15,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                ),
            remoteMediator = getOrCreateMediator(feedId, feedUri),
        ) {
            feedDao.getPostsForFeed(feedId)
        }.flow.map { data -> data.map { it.toPost() } }.flowOn(appDispatchers.io)
    }
}
