/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import app.bsky.actor.GetProfileResponse
import app.bsky.feed.GetAuthorFeedResponse
import com.contexts.pulse.data.local.database.dao.ProfileDao
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.local.database.entities.ProfileEntity
import com.contexts.pulse.data.local.database.entities.toProfileEntity
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.exceptions.NetworkError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

class ProfileRepositoryImpl(
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

    override suspend fun getProfileFeed(): Response<GetAuthorFeedResponse, NetworkError> {
        val current = preferencesRepository.getCurrentUser()
        return profileAPI.getAuthorFeed(current ?: "")
    }

    override suspend fun getSavedFeeds(did: String): Response<List<FeedEntity>, NetworkError> {
        return profileAPI.getSavedFeeds(did)
    }

    override suspend fun insertProfile(profile: ProfileEntity) {
        profileDao.insertProfile(profile)
    }
}
