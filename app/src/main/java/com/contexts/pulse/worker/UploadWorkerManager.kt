/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.worker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.domain.repository.PendingUploadRepository

class UploadWorkerManager(
    private val pendingUploadRepository: PendingUploadRepository,
    private val workManager: WorkManager,
) {
    suspend fun upload(uploadId: Long) {
        val upload = pendingUploadRepository.getPendingUploadsWithMediaById(uploadId)
        val media = upload?.mediaAttachments
        val inputData = workDataOf("uploadId" to uploadId)
        val createPostWork =
            OneTimeWorkRequestBuilder<CreatePostWorker>()
                .setInputData(inputData)
                .build()

        if (!media.isNullOrEmpty()) {
            if (media.first().type == MediaType.IMAGE) {
                val uploadBlobWork =
                    OneTimeWorkRequestBuilder<UploadBlobWorker>()
                        .setInputData(inputData)
                        .build()
                workManager
                    .beginWith(uploadBlobWork)
                    .then(createPostWork)
                    .enqueue()
            } else {
                val uploadVideoWork =
                    OneTimeWorkRequestBuilder<UploadVideoWorker>()
                        .setInputData(inputData)
                        .build()
                val jobStatusWork =
                    OneTimeWorkRequestBuilder<JobStatusWorker>()
                        .setInputData(inputData)
                        .build()
                workManager
                    .beginWith(uploadVideoWork)
                    .then(jobStatusWork)
                    .then(createPostWork)
                    .enqueue()
            }
        } else {
            workManager.enqueue(createPostWork)
        }
    }
}
