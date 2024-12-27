/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.bsky.embed.AspectRatio

@Entity(
    tableName = "pending_media_attachments",
    foreignKeys = [
        ForeignKey(
            entity = PendingUploadEntity::class,
            parentColumns = ["id"],
            childColumns = ["uploadId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("uploadId")],
)
data class PendingMediaAttachment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uploadId: Long,
    val type: MediaType,
    val localUri: String,
    val mimeType: String? = null,
    val altText: String? = null,
    val uploadState: MediaUploadState = MediaUploadState.PENDING,
    val remoteLink: String? = null,
    val videoJobId: String? = null,
    val aspectRatio: AspectRatio? = null,
    val videoProcessingState: VideoProcessingState = VideoProcessingState.NONE,
    val videoProcessingProgress: Long? = 0L,
    val videoProcessingError: String? = null,
) {
    val isFullyReady: Boolean
        get() =
            uploadState == MediaUploadState.UPLOADED &&
                (!type.requiresProcessing() || videoProcessingState == VideoProcessingState.JOB_STATE_COMPLETED)
}

enum class MediaType {
    IMAGE,
    VIDEO,
    ;

    fun requiresProcessing(): Boolean = this == VIDEO
}

enum class MediaUploadState {
    PENDING,
    UPLOADING,
    UPLOADED,
    FAILED,
}

enum class VideoProcessingState {
    NONE,
    PROCESSING,
    JOB_STATE_COMPLETED,
    JOB_STATE_FAILED,
}
