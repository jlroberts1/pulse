/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.local.datastore.PreferencesDataStore
import org.koin.dsl.module

val localDataModule =
    module {
        single<DataStore<Preferences>>(createdAtStart = false) {
            PreferencesDataStore(get()).dataStore
        }
        single<PulseDatabase>(createdAtStart = false) {
            Room.databaseBuilder(get(), PulseDatabase::class.java, "pulse.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
