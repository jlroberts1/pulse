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
import app.bsky.feed.GetPostsResponse
import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.GetJobStatusResponse
import app.bsky.video.UploadVideoResponse
import com.atproto.repo.CreateRecordResponse
import com.atproto.repo.UploadBlobResponse
import com.contexts.pulse.data.network.api.PostAPI
import com.contexts.pulse.data.network.api.UploadAPI
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.model.CreateLikeRecordRequest
import com.contexts.pulse.domain.model.CreateLikeRecordResponse
import com.contexts.pulse.domain.model.CreateRecord
import com.contexts.pulse.domain.model.UnlikeRecordRequest
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.withContext
import sh.christian.ozone.api.AtUri

class PostRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val postAPI: PostAPI,
    private val uploadAPI: UploadAPI,
) : PostRepository {
    override suspend fun getPostThread(uri: String): Response<GetPostThreadResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.getPostThread(uri)
        }

    override suspend fun createPost(createRecord: CreateRecord): Response<CreateRecordResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.createPost(createRecord)
        }

    override suspend fun uploadBlob(
        params: UploadParams,
        onUploadProgress: suspend (Float) -> Unit,
    ): Response<UploadBlobResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.uploadBlob(params, onUploadProgress)
        }

    override suspend fun uploadVideo(
        params: UploadParams,
        onUploadProgress: suspend (Float) -> Unit,
    ): Response<UploadVideoResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.uploadVideo(params, onUploadProgress)
        }

    override suspend fun getVideoProcessingStatus(
        getJobStatusQueryParams: GetJobStatusQueryParams,
    ): Response<GetJobStatusResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.getVideoProcessingStatus(getJobStatusQueryParams)
        }

    override suspend fun getPosts(uris: List<AtUri>): Response<GetPostsResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.getPosts(uris)
        }

    override suspend fun likePost(createLikeRecordRequest: CreateLikeRecordRequest): Response<CreateLikeRecordResponse, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.likePost(createLikeRecordRequest)
        }

    override suspend fun unlikePost(unlikeRecordRequest: UnlikeRecordRequest): Response<Unit, NetworkError> =
        withContext(appDispatchers.io) {
            postAPI.unlikePost(unlikeRecordRequest)
        }
}
