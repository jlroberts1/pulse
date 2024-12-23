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

@Entity(
    tableName = "pending_external_embeds",
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
data class PendingExternalEmbed(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uploadId: Long,
    val uri: String,
    val title: String?,
    val description: String?,
    val thumbnailUri: String?,
    val thumbnailMimeType: String?,
    val thumbnailSize: Long?,
)
