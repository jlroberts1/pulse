/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.di

import app.cash.sqldelight.db.SqlDriver
import com.contexts.cosmic.MainViewModel
import com.contexts.cosmic.data.local.InstantAdapter
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.local.SqldelightDataSource
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.data.network.httpclient.AuthManagerImpl
import com.contexts.cosmic.data.network.httpclient.KTorHttpClientImpl
import com.contexts.cosmic.data.network.httpclient.TokenRefreshManager
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.FeedRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.data.repository.ProfileRepositoryImpl
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.db.User
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.screens.home.HomeViewModel
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import com.contexts.cosmic.ui.screens.profile.ProfileViewModel
import com.contexts.cosmic.ui.screens.settings.SettingsViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single<SnackbarDelegate> { SnackbarDelegate() }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
        single { User.Adapter(indexedAtAdapter = InstantAdapter) }
        single<Database> { Database(get<SqlDriver>(), get()) }
        single<LocalDataSource> { SqldelightDataSource(get<Database>()) }
        single<AuthManager> { AuthManagerImpl(get<LocalDataSource>()) }
        single<TokenRefreshManager> { TokenRefreshManager() }
        single<HttpClient> {
            KTorHttpClientImpl(
                get<AuthManager>(),
                get<TokenRefreshManager>(),
            ).client
        }

        single<ProfileAPI> { ProfileAPI(get<HttpClient>()) }
        single<FeedAPI> { FeedAPI(get<HttpClient>()) }
        single<AuthenticateAPI> { AuthenticateAPI(get<HttpClient>()) }

        single<ProfileRepository> {
            ProfileRepositoryImpl(get<ProfileAPI>(), get<LocalDataSource>())
        }
        single<FeedRepository> {
            FeedRepositoryImpl(get<FeedAPI>())
        }
        single<AuthenticateRepository> {
            AuthenticateRepositoryImpl(
                get<AuthenticateAPI>(),
                get<ProfileAPI>(),
                get<LocalDataSource>(),
            )
        }

        viewModel { MainViewModel(get(), get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { LoginViewModel(get<AuthenticateRepository>()) }
        viewModel { ProfileViewModel(get<ProfileRepository>(), get<AuthManager>()) }
    }

expect val platformModule: Module
expect val sqlDriverModule: Module

internal fun getBaseModules() =
    listOf(
        platformModule,
        sqlDriverModule,
        appModule,
    )

fun initializeKoin(additionalModules: List<Module>) {
    startKoin {
        modules(getBaseModules() + additionalModules)
    }
}
