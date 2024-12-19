/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import app.bsky.actor.GetProfileResponse
import app.bsky.feed.GetAuthorFeedResponse
import com.contexts.cosmic.data.local.database.dao.ProfileDao
import com.contexts.cosmic.data.local.database.entities.FeedEntity
import com.contexts.cosmic.data.local.database.entities.ProfileEntity
import com.contexts.cosmic.data.local.database.entities.toProfileEntity
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.data.network.client.onSuccess
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.exceptions.NetworkError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

class ProfileRepositoryImpl(
    private val profileAPI: ProfileAPI,
    private val profileDao: ProfileDao,
    private val preferencesRepository: PreferencesRepository,
) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> = profileAPI.getProfile(actor)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMyProfile(): Flow<ProfileEntity?> =
        preferencesRepository.getCurrentUserFlow().flatMapLatest {
            profileAPI.getProfile(it).onSuccess { response ->
                profileDao.insertProfile(response.toProfileEntity())
            }
            profileDao.getProfileByDid(it)
        }

    override suspend fun getProfileFeed(): Response<GetAuthorFeedResponse, NetworkError> {
        val current = preferencesRepository.getCurrentUserFlow().first()
        return profileAPI.getAuthorFeed(current)
    }

    override suspend fun getSavedFeeds(did: String): Response<List<FeedEntity>, NetworkError> {
        return profileAPI.getSavedFeeds(did)
    }

    override suspend fun insertProfile(profile: ProfileEntity) {
        profileDao.insertProfile(profile)
    }
}
