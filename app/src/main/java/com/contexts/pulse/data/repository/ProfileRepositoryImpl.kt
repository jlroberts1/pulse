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
import app.bsky.actor.GetProfileResponse
import app.bsky.feed.FeedViewPost
import app.bsky.feed.GetAuthorFeedResponse
import com.contexts.pulse.data.local.database.dao.ProfileDao
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.local.database.entities.ProfileEntity
import com.contexts.pulse.data.local.database.entities.toProfileEntity
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.NetworkPagingSource
import com.contexts.pulse.domain.repository.PagedRequest
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.RequestResult
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull

class ProfileRepositoryImpl(
    private val client: HttpClient,
    private val profileAPI: ProfileAPI,
    private val profileDao: ProfileDao,
    private val preferencesRepository: PreferencesRepository,
) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> = profileAPI.getProfile(actor)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMyProfile(): Flow<ProfileEntity?> =
        preferencesRepository.getCurrentUserFlow().mapNotNull { it }.flatMapLatest {
            profileAPI.getProfile(it).onSuccess { response ->
                profileDao.insertProfile(response.toProfileEntity())
            }
            profileDao.getProfileByDid(it)
        }

    override suspend fun getProfileFeed(): RequestResult<Flow<PagingData<FeedViewPost>>> {
        val current = preferencesRepository.getCurrentUser() ?: return RequestResult.NoCurrentUser
        val request = PagedRequest.AuthorFeed(current)
        return RequestResult.Success(
            Pager(
                config =
                    PagingConfig(
                        pageSize = request.limit,
                        enablePlaceholders = false,
                    ),
                pagingSourceFactory = {
                    NetworkPagingSource<FeedViewPost, GetAuthorFeedResponse>(
                        client = client,
                        request = request,
                        type = typeInfo<GetAuthorFeedResponse>(),
                        getItems = { it.feed },
                        getCursor = { it.cursor },
                    )
                },
            ).flow.flowOn(Dispatchers.IO),
        )
    }

    override suspend fun getSavedFeeds(did: String): Response<List<FeedEntity>, NetworkError> {
        return profileAPI.getSavedFeeds(did)
    }

    override suspend fun insertProfile(profile: ProfileEntity) {
        profileDao.insertProfile(profile)
    }
}
