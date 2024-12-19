/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import androidx.room.Room
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.dao.FeedPostDao
import com.contexts.pulse.data.local.database.dao.ProfileDao
import com.contexts.pulse.data.local.database.dao.RemoteKeysDao
import com.contexts.pulse.data.local.database.dao.UserDao
import com.contexts.pulse.data.local.datastore.PreferencesDataStore
import org.koin.dsl.module

val localDataModule =
    module {
        single { PreferencesDataStore(get()).dataStore }
        single<PulseDatabase> {
            Room.databaseBuilder(get(), PulseDatabase::class.java, "pulse.db")
                .fallbackToDestructiveMigration()
                .build()
        }
        single<RemoteKeysDao> { get<PulseDatabase>().remoteKeysDao() }
        single<FeedDao> { get<PulseDatabase>().feedDao() }
        single<FeedPostDao> { get<PulseDatabase>().feedPostDao() }
        single<ProfileDao> { get<PulseDatabase>().profileDao() }
        single<UserDao> { get<PulseDatabase>().userDao() }
    }
