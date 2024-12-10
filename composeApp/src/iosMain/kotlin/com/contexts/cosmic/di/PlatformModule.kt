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
