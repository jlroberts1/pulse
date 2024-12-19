/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.contexts.cosmic.data.local.database.converters.FeedConverters
import com.contexts.cosmic.data.local.database.converters.InstantConvertor
import com.contexts.cosmic.data.local.database.converters.ServiceConverter
import com.contexts.cosmic.data.local.database.converters.StringListConverter
import com.contexts.cosmic.data.local.database.converters.VerificationMethodConverter
import com.contexts.cosmic.data.local.database.dao.FeedDao
import com.contexts.cosmic.data.local.database.dao.FeedPostDao
import com.contexts.cosmic.data.local.database.dao.ProfileDao
import com.contexts.cosmic.data.local.database.dao.RemoteKeysDao
import com.contexts.cosmic.data.local.database.dao.UserDao
import com.contexts.cosmic.data.local.database.entities.FeedEntity
import com.contexts.cosmic.data.local.database.entities.FeedPostEntity
import com.contexts.cosmic.data.local.database.entities.ProfileEntity
import com.contexts.cosmic.data.local.database.entities.RemoteKeys
import com.contexts.cosmic.data.local.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FeedEntity::class,
        FeedPostEntity::class,
        RemoteKeys::class,
        ProfileEntity::class,
    ],
    version = 1,
)
@TypeConverters(
    StringListConverter::class,
    VerificationMethodConverter::class,
    ServiceConverter::class,
    InstantConvertor::class,
    FeedConverters::class,
)
abstract class CosmicDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun feedDao(): FeedDao

    abstract fun feedPostDao(): FeedPostDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    abstract fun profileDao(): ProfileDao
}
