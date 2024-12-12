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
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.handleInChannel
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.domain.model.toUser
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ProfileRepositoryImpl(
    private val profileAPI: ProfileAPI,
    private val localDataSource: LocalDataSource,
) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> {
        return profileAPI.getProfile(actor)
    }

    override suspend fun getMyProfile(myDid: String): Flow<RequestResult<User, AppError>> =
        channelFlow {
            trySend(RequestResult.Loading)
            localDataSource.getUser(myDid)?.let { trySend(RequestResult.Success(it)) }
            profileAPI.getMyProfile(myDid)
                .handleInChannel(
                    channelScope = this,
                    transform = { it.toUser() },
                    saveAction = { localDataSource.updateProfile(it) },
                )
        }

    override suspend fun getProfileFeed(myDid: String): Response<GetAuthorFeedResponse, NetworkError> {
        return profileAPI.getAuthorFeed(myDid)
    }
}
