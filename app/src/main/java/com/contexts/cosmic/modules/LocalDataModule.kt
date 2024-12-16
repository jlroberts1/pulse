/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.modules

import androidx.room.Room
import com.contexts.cosmic.data.local.database.CosmicDatabase
import com.contexts.cosmic.data.local.database.dao.UserDao
import com.contexts.cosmic.data.local.datastore.PreferencesDataStore
import org.koin.dsl.module

val localDataModule =
    module {
        single { PreferencesDataStore(get()).dataStore }
        single<CosmicDatabase> {
            Room.databaseBuilder(get(), CosmicDatabase::class.java, "cosmic.db")
                .fallbackToDestructiveMigration()
                .build()
        }
        single<UserDao> { get<CosmicDatabase>().userDao() }
    }
