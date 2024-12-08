package com.contexts.cosmic.di

import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.datastore.PreferencesDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<PreferencesDataSource> { PreferencesDataSourceImpl() }
    }