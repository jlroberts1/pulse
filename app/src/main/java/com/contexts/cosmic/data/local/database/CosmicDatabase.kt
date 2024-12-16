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
import com.contexts.cosmic.data.local.database.converters.ServiceConverter
import com.contexts.cosmic.data.local.database.converters.StringListConverter
import com.contexts.cosmic.data.local.database.converters.VerificationMethodConverter
import com.contexts.cosmic.data.local.database.dao.UserDao
import com.contexts.cosmic.data.local.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
    ],
    version = 1,
)
@TypeConverters(StringListConverter::class, VerificationMethodConverter::class, ServiceConverter::class)
abstract class CosmicDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
