/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.contexts.pulse.data.local.database.converters.Converters
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.dao.PendingUploadDao
import com.contexts.pulse.data.local.database.dao.ProfileDao
import com.contexts.pulse.data.local.database.dao.UserDao
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.local.database.entities.PendingExternalEmbed
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.local.database.entities.ProfileEntity
import com.contexts.pulse.data.local.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FeedEntity::class,
        ProfileEntity::class,
        PendingUploadEntity::class,
        PendingMediaAttachment::class,
        PendingExternalEmbed::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class PulseDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun feedDao(): FeedDao

    abstract fun profileDao(): ProfileDao

    abstract fun pendingUploadDao(): PendingUploadDao
}
