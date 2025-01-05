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
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import app.bsky.actor.Type
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.dao.RemoteKeysDao
import com.contexts.pulse.data.local.database.entities.RemoteKeysEntity
import com.contexts.pulse.data.local.database.entities.TimelinePostEntity
import com.contexts.pulse.data.local.database.entities.toTimelinePostEntity
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.withContext
import logcat.logcat

@OptIn(ExperimentalPagingApi::class)
class TimelineRemoteMediator(
    private val feedId: String,
    private val feedUri: String,
    private val feedType: Type,
    private val appDispatchers: AppDispatchers,
    private val feedDao: FeedDao,
    private val feedRepository: FeedRepository,
    private val remoteKeysDao: RemoteKeysDao,
    private val db: PulseDatabase,
) : RemoteMediator<Int, TimelinePostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TimelinePostEntity>,
    ): MediatorResult =
        withContext(appDispatchers.io) {
            try {
                val loadKey =
                    when (loadType) {
                        LoadType.REFRESH -> null
                        LoadType.PREPEND -> return@withContext MediatorResult.Success(
                            endOfPaginationReached = true,
                        )

                        LoadType.APPEND -> {
                            val remoteKeys = getRemoteKey()
                            if (remoteKeys?.nextKey == null) {
                                return@withContext MediatorResult.Success(endOfPaginationReached = true)
                            }
                            remoteKeys.nextKey
                        }
                    }

                val result =
                    when (feedType) {
                        is Type.Feed -> {
                            when (val response = feedRepository.getFeed(feedUri = feedUri, cursor = loadKey)) {
                                is Response.Success -> {
                                    handleSuccess(
                                        loadType,
                                        MediatedFeedResponse(
                                            cursor = response.data.cursor,
                                            feed = response.data.feed.map { it.toTimelinePostEntity(feedId) },
                                        ),
                                    )
                                    MediatorResult.Success(endOfPaginationReached = response.data.feed.isEmpty())
                                }
                                is Response.Error -> {
                                    logcat { "Error fetching feed, ${response.error.message}" }
                                    MediatorResult.Error(Throwable(response.error.message))
                                }
                            }
                        }

                        is Type.Timeline -> {
                            when (val response = feedRepository.getTimeline(cursor = loadKey)) {
                                is Response.Success -> {
                                    handleSuccess(
                                        loadType,
                                        MediatedFeedResponse(
                                            cursor = response.data.cursor,
                                            feed = response.data.feed.map { it.toTimelinePostEntity(feedId) },
                                        ),
                                    )
                                    MediatorResult.Success(endOfPaginationReached = response.data.feed.isEmpty())
                                }

                                is Response.Error -> {
                                    logcat { "Error fetching feed, ${response.error.message}" }
                                    MediatorResult.Error(Throwable(response.error.message))
                                }
                            }
                        }

                        is Type.List -> {
                            when (val response = feedRepository.getListFeed(feedUri = feedUri, cursor = loadKey)) {
                                is Response.Success -> {
                                    handleSuccess(
                                        loadType,
                                        MediatedFeedResponse(
                                            cursor = response.data.cursor,
                                            feed = response.data.feed.map { it.toTimelinePostEntity(feedId) },
                                        ),
                                    )
                                    MediatorResult.Success(endOfPaginationReached = response.data.feed.isEmpty())
                                }
                                is Response.Error -> {
                                    logcat { "Error fetching feed, ${response.error.message}" }
                                    MediatorResult.Error(Throwable(response.error.message))
                                }
                            }
                        }
                    }
                return@withContext result
            } catch (e: Exception) {
                logcat { "Error fetching feed, ${e.message}" }
                return@withContext MediatorResult.Error(e)
            }
        }

    private suspend fun handleSuccess(
        loadType: LoadType,
        data: MediatedFeedResponse,
    ) {
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                remoteKeysDao.clearKeysByFeed(feedId)
                feedDao.clearFeed(feedId)
            }
            val key =
                RemoteKeysEntity(
                    feedId = feedId,
                    prevKey = null,
                    nextKey = data.cursor,
                )
            remoteKeysDao.insertKey(key)
            val posts = data.feed
            feedDao.insertPosts(posts)
        }
    }

    data class MediatedFeedResponse(
        val cursor: String?,
        val feed: List<TimelinePostEntity>,
    )

    private suspend fun getRemoteKey(): RemoteKeysEntity? {
        return remoteKeysDao.getRemoteKeyForFeed(feedId)
    }
}
