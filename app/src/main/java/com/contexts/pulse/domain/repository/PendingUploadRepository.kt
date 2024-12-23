/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.GetJobStatusResponse
import app.bsky.video.UploadVideoResponse
import com.atproto.repo.UploadBlobResponse
import com.contexts.pulse.data.local.database.entities.PendingExternalEmbed
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.local.database.relations.PendingUploadWithMedia
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.exceptions.NetworkError

interface PendingUploadRepository {
    suspend fun getPendingUploadsWithMedia(): List<PendingUploadWithMedia>?

    suspend fun getPendingUploadById(id: Long): PendingUploadEntity?

    suspend fun insertUpload(upload: PendingUploadEntity): Long

    suspend fun insertMediaAttachment(attachment: PendingMediaAttachment)

    suspend fun insertExternalEmbed(embed: PendingExternalEmbed)

    suspend fun updateUpload(upload: PendingUploadEntity)

    suspend fun updateMediaAttachment(attachment: PendingMediaAttachment)

    suspend fun deleteUpload(upload: PendingUploadEntity)

    suspend fun uploadVideo(
        uploadParams: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadVideoResponse, NetworkError>

    suspend fun uploadBlob(
        uploadParams: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadBlobResponse, NetworkError>

    suspend fun getVideoProcessingStatus(getJobStatusQueryParams: GetJobStatusQueryParams): Response<GetJobStatusResponse, NetworkError>
}
