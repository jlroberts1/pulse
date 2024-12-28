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
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Entity(tableName = "pending_uploads")
data class PendingUploadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userDid: String,
    val text: String,
    val replyReference: ReplyReference? = null,
    val createdAt: Instant = Clock.System.now(),
    val uploadState: PostUploadState = PostUploadState.PENDING,
    val errorMessage: String? = null,
)

enum class PostUploadState {
    PENDING,
    UPLOADING,
    FAILED,
}

@Serializable
data class ReplyReference(
    val root: ReplyLink,
    val parent: ReplyLink,
)

@Serializable
data class ReplyLink(
    val uri: String?,
    val cid: String?,
)
