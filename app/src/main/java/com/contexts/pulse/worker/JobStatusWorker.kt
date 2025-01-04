/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.State
import com.contexts.pulse.data.local.database.entities.VideoProcessingState
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.extensions.getRemoteLink
import org.koin.core.component.KoinComponent

class JobStatusWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val pendingUploadRepository: PendingUploadRepository,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParameters), KoinComponent {
    override suspend fun doWork(): Result {
        val uploadId = inputData.getLong("uploadId", 0L)
        val jobId = inputData.getString("jobId") ?: return Result.failure()
        if (uploadId == 0L) return Result.failure()

        val post = pendingUploadRepository.getPendingUploadsWithMediaById(uploadId)
        val mediaAttachment = post?.mediaAttachments?.first() ?: return Result.failure()
        val params = GetJobStatusQueryParams(jobId = jobId)
        when (val response = postRepository.getVideoProcessingStatus(params)) {
            is Response.Success -> {
                if (response.data.jobStatus.blob == null) return Result.retry()
                return when (response.data.jobStatus.state) {
                    is State.JOBSTATECOMPLETED -> {
                        pendingUploadRepository.updateMediaAttachment(
                            mediaAttachment.copy(
                                videoProcessingState = VideoProcessingState.JOB_STATE_COMPLETED,
                                videoProcessingProgress = response.data.jobStatus.progress,
                                remoteLink = response.data.jobStatus.blob.getRemoteLink(),
                            ),
                        )
                        Result.success(
                            workDataOf(
                                "blobRef" to response.data.jobStatus.blob.getRemoteLink(),
                                "uploadId" to uploadId,
                            ),
                        )
                    }

                    is State.JOBSTATEFAILED -> {
                        pendingUploadRepository.updateMediaAttachment(
                            mediaAttachment.copy(
                                videoProcessingState = VideoProcessingState.JOB_STATE_FAILED,
                                videoProcessingProgress = response.data.jobStatus.progress,
                            ),
                        )
                        Result.failure()
                    }
                }
            }

            is Response.Error -> return Result.failure()
        }
    }
}
