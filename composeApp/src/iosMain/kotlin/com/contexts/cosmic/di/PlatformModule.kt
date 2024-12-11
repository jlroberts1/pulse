/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.datastore.PreferencesDataSourceImpl
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.util.UrlHandler
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<PreferencesDataSource> { PreferencesDataSourceImpl() }
        single<UrlHandler> { UrlHandler() }
    }

actual val sqlDriverModule: Module =
    module {
        single {
            NativeSqliteDriver(Database.Schema, "cosmic.db")
        }
    }
