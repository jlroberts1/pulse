/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.GetJobStatusResponse
import app.bsky.video.UploadVideoResponse
import com.atproto.repo.UploadBlobResponse
import com.contexts.pulse.data.local.database.dao.PendingUploadDao
import com.contexts.pulse.data.local.database.entities.PendingExternalEmbed
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.local.database.relations.PendingUploadWithMedia
import com.contexts.pulse.data.network.api.UploadAPI
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.withContext

class PendingUploadRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val pendingUploadDao: PendingUploadDao,
    private val uploadAPI: UploadAPI,
) : PendingUploadRepository {
    override suspend fun getPendingUploadsWithMedia(): List<PendingUploadWithMedia> =
        withContext(appDispatchers.io) {
            pendingUploadDao.getPendingUploadsWithMedia()
        }

    override suspend fun insertUpload(upload: PendingUploadEntity): Long =
        withContext(appDispatchers.io) {
            pendingUploadDao.insertUpload(upload)
        }

    override suspend fun insertMediaAttachment(attachment: PendingMediaAttachment) =
        withContext(appDispatchers.io) {
            pendingUploadDao.insertMediaAttachment(attachment)
        }

    override suspend fun insertExternalEmbed(embed: PendingExternalEmbed) =
        withContext(appDispatchers.io) {
            pendingUploadDao.insertExternalEmbed(embed)
        }

    override suspend fun updateUpload(upload: PendingUploadEntity) =
        withContext(appDispatchers.io) {
            pendingUploadDao.updateUpload(upload)
        }

    override suspend fun updateMediaAttachment(attachment: PendingMediaAttachment) =
        withContext(appDispatchers.io) {
            pendingUploadDao.updateMediaAttachment(attachment)
        }

    override suspend fun deleteUpload(upload: PendingUploadEntity) =
        withContext(appDispatchers.io) {
            pendingUploadDao.deleteUpload(upload)
        }

    override suspend fun uploadVideo(
        uploadParams: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadVideoResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.uploadVideo(uploadParams, onUploadProgress)
        }

    override suspend fun uploadBlob(
        uploadParams: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadBlobResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.uploadBlob(uploadParams, onUploadProgress)
        }

    override suspend fun getVideoProcessingStatus(
        getJobStatusQueryParams: GetJobStatusQueryParams,
    ): Response<GetJobStatusResponse, NetworkError> =
        withContext(appDispatchers.io) {
            uploadAPI.getVideoProcessingStatus(getJobStatusQueryParams)
        }
}
