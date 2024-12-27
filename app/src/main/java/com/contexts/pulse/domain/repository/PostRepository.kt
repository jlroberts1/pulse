/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import app.bsky.feed.GetPostThreadResponse
import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.GetJobStatusResponse
import app.bsky.video.UploadVideoResponse
import com.atproto.repo.CreateRecordResponse
import com.atproto.repo.UploadBlobResponse
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.model.CreateRecord
import com.contexts.pulse.exceptions.NetworkError

interface PostRepository {
    suspend fun getPostThread(uri: String): Response<GetPostThreadResponse, NetworkError>

    suspend fun createPost(createRecord: CreateRecord): Response<CreateRecordResponse, NetworkError>

    suspend fun uploadBlob(
        params: UploadParams,
        onUploadProgress: suspend (Float) -> Unit,
    ): Response<UploadBlobResponse, NetworkError>

    suspend fun uploadVideo(
        params: UploadParams,
        onUploadProgress: suspend (Float) -> Unit,
    ): Response<UploadVideoResponse, NetworkError>

    suspend fun getVideoProcessingStatus(getJobStatusQueryParams: GetJobStatusQueryParams): Response<GetJobStatusResponse, NetworkError>
}
