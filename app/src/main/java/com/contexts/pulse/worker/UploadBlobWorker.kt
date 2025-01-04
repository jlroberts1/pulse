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
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.contexts.pulse.R
import com.contexts.pulse.data.network.api.UploadParams
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.extensions.copyUriToTempFile
import com.contexts.pulse.extensions.getAspectRatio
import com.contexts.pulse.extensions.getMimeType
import org.koin.core.component.KoinComponent
import sh.christian.ozone.api.model.Blob

class UploadBlobWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val pendingUploadRepository: PendingUploadRepository,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParameters), KoinComponent {
    private val uploadId = workerParameters.inputData.getLong("uploadId", 0L)

    override suspend fun doWork(): Result {
        if (uploadId == 0L) {
            return Result.failure(
                workDataOf(
                    "error" to "No upload id received",
                ),
            )
        }

        val post =
            pendingUploadRepository.getPendingUploadsWithMediaById(uploadId)
                ?: return Result.failure(
                    workDataOf(
                        "error" to "Unable to find upload",
                    ),
                )

        var totalProgress: Float
        val totalFiles = post.mediaAttachments.size
        try {
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
            post.mediaAttachments.forEach { mediaAttachment ->
                val tempFileResult =
                    copyUriToTempFile(applicationContext, Uri.parse(mediaAttachment.localUri))
                tempFileResult.fold(
                    onSuccess = { tempFile ->
                        val mimeType =
                            tempFile.getMimeType(applicationContext) ?: return Result.failure()
                        val uploadParams =
                            UploadParams(
                                file = tempFile,
                                mimeType = mimeType,
                            )
                        val response =
                            postRepository.uploadBlob(uploadParams) { progress ->
                                updateProgress(progress)
                            }
                        when (response) {
                            is Response.Success -> {
                                val blob = response.data.blob as? Blob.StandardBlob
                                pendingUploadRepository.updateMediaAttachment(
                                    mediaAttachment.copy(
                                        remoteLink = blob?.ref?.link?.cid,
                                        mimeType = mimeType,
                                        size = blob?.size,
                                        aspectRatio = tempFile.getAspectRatio(context = applicationContext),
                                    ),
                                )
                                return Result.success()
                            }

                            is Response.Error -> throw Throwable(response.error.message)
                        }
                    },
                    onFailure = {
                        return Result.failure()
                    },
                )
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
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

        if (Build.VERSION.SDK_INT >= 34) {
            setForegroundAsync(
                ForegroundInfo(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
                ),
            )
        } else {
            setForegroundAsync(
                ForegroundInfo(
                    NOTIFICATION_ID,
                    notification,
                ),
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "upload_channel"
        const val NOTIFICATION_ID = 2
    }
}
