package com.contexts.cosmic.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.datastore.PreferencesDataSourceImpl
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.util.UrlHandler
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<PreferencesDataSource> { PreferencesDataSourceImpl(get()) }
        single<UrlHandler> { UrlHandler(get()) }
    }

actual val sqlDriverModule: Module =
    module {
        single<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(),
                name = "cosmic.db",
            )
        }
    }
