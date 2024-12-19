/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.repository

import app.bsky.actor.GetProfileResponse
import app.bsky.feed.GetAuthorFeedResponse
import com.contexts.cosmic.data.local.database.entities.FeedEntity
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.exceptions.NetworkError

interface ProfileRepository {
    suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError>

    suspend fun getMyProfile(): Response<GetProfileResponse, NetworkError>

    suspend fun getProfileFeed(): Response<GetAuthorFeedResponse, NetworkError>

    suspend fun getSavedFeeds(): Response<List<FeedEntity>, NetworkError>
}
