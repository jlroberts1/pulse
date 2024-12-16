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
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.exceptions.NetworkError

class ProfileRepositoryImpl(
    private val profileAPI: ProfileAPI,
) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> = profileAPI.getProfile(actor)

    override suspend fun getProfileFeed(myDid: String): Response<GetAuthorFeedResponse, NetworkError> = profileAPI.getAuthorFeed(myDid)
}
