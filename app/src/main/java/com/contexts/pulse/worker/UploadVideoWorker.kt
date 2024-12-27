/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.bsky.video.JobStatus
import app.bsky.video.State
import com.contexts.pulse.R
import com.contexts.pulse.data.local.database.entities.MediaUploadState
import com.contexts.pulse.data.local.database.entities.VideoProcessingState
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.extensions.getAspectRatio
import com.contexts.pulse.extensions.getMimeType
import com.contexts.pulse.extensions.getRemoteLink
import org.koin.core.component.KoinComponent
import java.io.File

class UploadVideoWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val pendingUploadRepository: PendingUploadRepository,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParameters), KoinComponent {
    private val uploadId = workerParameters.inputData.getLong("uploadId", 0L)

    override suspend fun doWork(): Result {
        if (uploadId == 0L) return Result.failure(workDataOf())

        val post =
            pendingUploadRepository.getPendingUploadsWithMediaById(uploadId)
                ?: return Result.failure(workDataOf())

        var totalProgress = 0f
        val totalFiles = post.mediaAttachments.size
        var jobStatus: JobStatus? = null

        try {
            val mediaAttachment = post.mediaAttachments.first()
            pendingUploadRepository.updateMediaAttachment(
                mediaAttachment.copy(
                    uploadState = MediaUploadState.UPLOADING,
                ),
            )

            suspend fun updateProgress(currentProgress: Float) {
                totalProgress = (currentProgress / totalFiles)
                val progressData =
                    workDataOf(
                        "progress" to totalProgress.toInt(),
                        "uploadId" to uploadId,
                    )
                setProgress(progressData)
                createForegroundInfo(totalProgress.toInt())
            }

            val file = File(mediaAttachment.localUri)
            val mimeType = file.getMimeType(applicationContext) ?: return Result.failure()
            val uploadParams =
                UploadParams(
                    file = File(mediaAttachment.localUri),
                    mimeType = mimeType,
                )

            val response =
                postRepository.uploadVideo(uploadParams) { progress ->
                    updateProgress(progress)
                }
            when (response) {
                is Response.Success -> {
                    val job = response.data.jobStatus
                    jobStatus = job
                    val videoProcessingState =
                        when (job.state) {
                            is State.JOBSTATECOMPLETED -> VideoProcessingState.JOB_STATE_COMPLETED
                            is State.JOBSTATEFAILED -> VideoProcessingState.JOB_STATE_FAILED
                            else -> VideoProcessingState.PROCESSING
                        }
                    pendingUploadRepository.updateMediaAttachment(
                        mediaAttachment.copy(
                            uploadState = MediaUploadState.UPLOADED,
                            videoJobId = job.jobId,
                            videoProcessingProgress = job.progress,
                            videoProcessingState = videoProcessingState,
                            aspectRatio = file.getAspectRatio(context = applicationContext),
                            mimeType = mimeType,
                            remoteLink = job.blob.getRemoteLink(),
                        ),
                    )
                }

                is Response.Error -> {
                    pendingUploadRepository.updateMediaAttachment(
                        mediaAttachment.copy(
                            uploadState = MediaUploadState.FAILED,
                        ),
                    )
                    throw Throwable(response.error.message)
                }
            }
            return Result.success(
                workDataOf(
                    "jobStatus" to jobStatus,
                ),
            )
        } catch (e: Exception) {
            return Result.failure(
                workDataOf(
                    "error" to e.message,
                    "uploadId" to uploadId,
                ),
            )
        }
    }

    private fun createForegroundInfo(progress: Int) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Upload Progress",
                NotificationManager.IMPORTANCE_LOW,
            )
        notificationManager.createNotificationChannel(channel)
        val notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Uploading Media")
                .setContentText("Upload Progress: $progress%")
                .setProgress(100, progress, false)
                .build()

        setForegroundAsync(ForegroundInfo(NOTIFICATION_ID, notification))
    }

    companion object {
        const val CHANNEL_ID = "upload_channel"
        const val NOTIFICATION_ID = 2
    }
}
