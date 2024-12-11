/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.ProfileDTO
import com.contexts.cosmic.domain.model.FeedResponse
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(actor: String): Response<ProfileDTO, NetworkError>

    suspend fun getMyProfile(myDid: String): Flow<RequestResult<User, AppError>>

    suspend fun getProfileFeed(myDid: String): Response<FeedResponse, NetworkError>
}
