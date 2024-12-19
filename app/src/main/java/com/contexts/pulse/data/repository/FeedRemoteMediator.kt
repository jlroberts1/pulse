/*
 * Copyright (c) 2024. James Roberts
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
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.local.database.dao.FeedPostDao
import com.contexts.pulse.data.local.database.dao.RemoteKeysDao
import com.contexts.pulse.data.local.database.entities.FeedPostEntity
import com.contexts.pulse.data.local.database.entities.RemoteKeys
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.FeedRepository

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator(
    private val feedId: String,
    private val feedUri: String,
    private val feedRepository: FeedRepository,
    private val feedPostDao: FeedPostDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val db: PulseDatabase,
) : RemoteMediator<Int, FeedPostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedPostEntity>,
    ): MediatorResult {
        try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKey()
                        if (remoteKeys?.nextKey == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                        remoteKeys.nextKey
                    }
                }
            when (val response = feedRepository.getFeed(feedUri = feedUri, cursor = loadKey)) {
                is Response.Success -> {
                    db.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeysDao.clearKeysByFeed(feedId)
                            feedPostDao.clearFeed(feedId)
                        }
                        val key =
                            RemoteKeys(
                                feedId = feedId,
                                prevKey = null,
                                nextKey = response.data.cursor,
                            )
                        remoteKeysDao.insertKey(key)
                        val posts = response.data.feed.map { FeedPostEntity.from(it, feedId) }
                        feedPostDao.insertPosts(posts)
                    }
                    return MediatorResult.Success(endOfPaginationReached = response.data.feed.isEmpty())
                }
                is Response.Error -> {
                    return MediatorResult.Error(Throwable(response.error.message))
                }
            }
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKey(): RemoteKeys? {
        return remoteKeysDao.getRemoteKeyForFeed(feedId)
    }
}
