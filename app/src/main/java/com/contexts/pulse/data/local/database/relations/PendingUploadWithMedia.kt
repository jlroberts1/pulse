/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.contexts.pulse.data.local.database.entities.PendingExternalEmbed
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity

data class PendingUploadWithMedia(
    @Embedded val upload: PendingUploadEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "uploadId",
    )
    val mediaAttachments: List<PendingMediaAttachment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "uploadId",
    )
    val externalEmbed: PendingExternalEmbed?,
)
