/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import app.bsky.feed.GetPostThreadResponse
import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.CreateRecordResponse
import com.contexts.pulse.data.network.api.PostAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.withContext

class PostRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val postAPI: PostAPI,
) : PostRepository {
    override suspend fun getPostThread(uri: String): Response<GetPostThreadResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.getPostThread(uri)
        }

    override suspend fun createPost(createRecordRequest: CreateRecordRequest): Response<CreateRecordResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.createPost(createRecordRequest)
        }
}
