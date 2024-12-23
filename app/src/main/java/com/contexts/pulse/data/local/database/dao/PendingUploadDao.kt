/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.contexts.pulse.data.local.database.entities.PendingExternalEmbed
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.local.database.relations.PendingUploadWithMedia

@Dao
interface PendingUploadDao {
    @Transaction
    @Query("SELECT * FROM pending_uploads")
    suspend fun getPendingUploadsWithMedia(): List<PendingUploadWithMedia>

    @Insert
    suspend fun insertUpload(upload: PendingUploadEntity): Long

    @Insert
    suspend fun insertMediaAttachment(attachment: PendingMediaAttachment)

    @Insert
    suspend fun insertExternalEmbed(embed: PendingExternalEmbed)

    @Update
    suspend fun updateUpload(upload: PendingUploadEntity)

    @Update
    suspend fun updateMediaAttachment(attachment: PendingMediaAttachment)

    @Delete
    suspend fun deleteUpload(upload: PendingUploadEntity)
}
