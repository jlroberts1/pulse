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
import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.local.DidDocumentAdapter
import com.contexts.cosmic.data.local.InstantAdapter
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.local.SqldelightDataSource
import com.contexts.cosmic.data.network.api.ActorAPI
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.ChatAPI
import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.network.api.NotificationsAPI
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.api.TenorAPI
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.data.network.httpclient.AuthManagerImpl
import com.contexts.cosmic.data.network.httpclient.KTorHttpClientImpl
import com.contexts.cosmic.data.network.httpclient.TokenRefreshManager
import com.contexts.cosmic.data.repository.ActorRepositoryImpl
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.ChatRepositoryImpl
import com.contexts.cosmic.data.repository.FeedRepositoryImpl
import com.contexts.cosmic.data.repository.NotificationsRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.data.repository.ProfileRepositoryImpl
import com.contexts.cosmic.data.repository.TenorRepositoryImpl
import com.contexts.cosmic.db.Auth_state
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.db.User
import com.contexts.cosmic.domain.repository.ActorRepository
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.ChatRepository
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.domain.repository.NotificationsRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.domain.repository.TenorRepository
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.screens.addpost.AddPostViewModel
import com.contexts.cosmic.ui.screens.chat.ChatViewModel
import com.contexts.cosmic.ui.screens.home.HomeViewModel
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import com.contexts.cosmic.ui.screens.notifications.NotificationsViewModel
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
    }

val databaseModule =
    module {
        single { User.Adapter(indexedAtAdapter = InstantAdapter) }
        single { Auth_state.Adapter(didDocumentAdapter = DidDocumentAdapter) }
        single<Database> {
            Database(
                get<SqlDriver>(),
                get<Auth_state.Adapter>(),
                get<User.Adapter>(),
            )
        }
        single<LocalDataSource> { SqldelightDataSource(get<Database>()) }
    }

val networkModule =
    module {
        single<AuthManager> { AuthManagerImpl(get<LocalDataSource>()) }
        single<TokenRefreshManager> { TokenRefreshManager() }
        single<HttpClient> { KTorHttpClientImpl(get<AuthManager>(), get<TokenRefreshManager>()).client }
    }

val apiModule =
    module {
        single<NotificationsAPI> { NotificationsAPI(get()) }
        single<ProfileAPI> { ProfileAPI(get<HttpClient>()) }
        single<FeedAPI> { FeedAPI(get<HttpClient>()) }
        single<AuthenticateAPI> { AuthenticateAPI(get<HttpClient>()) }
        single<ChatAPI> { ChatAPI(get<HttpClient>()) }
        single<ActorAPI> { ActorAPI(get<HttpClient>()) }
        single<TenorAPI> { TenorAPI() }
    }

val repositoryModule =
    module {
        single<PreferencesRepository> { PreferencesRepositoryImpl(get<PreferencesDataSource>()) }
        factory<AuthenticateRepository> {
            AuthenticateRepositoryImpl(get<AuthenticateAPI>(), get<ProfileAPI>(), get<LocalDataSource>())
        }
        factory<ProfileRepository> { ProfileRepositoryImpl(get<ProfileAPI>(), get<LocalDataSource>()) }
        factory<FeedRepository> { FeedRepositoryImpl(get<FeedAPI>()) }
        factory<NotificationsRepository> { NotificationsRepositoryImpl(get<NotificationsAPI>()) }
        factory<ChatRepository> { ChatRepositoryImpl(get<ChatAPI>()) }
        factory<ActorRepository> { ActorRepositoryImpl(get<ActorAPI>()) }
        factory<TenorRepository> { TenorRepositoryImpl(get<TenorAPI>()) }
    }

val viewModelModule =
    module {
        viewModel { MainViewModel(get<AuthManager>(), get<NotificationsRepository>(), get<PreferencesRepository>()) }
        viewModel { HomeViewModel(get<FeedRepository>()) }
        viewModel { SettingsViewModel(get<PreferencesRepository>()) }
        viewModel { NotificationsViewModel(get<NotificationsRepository>(), get<PreferencesRepository>()) }
        viewModel { LoginViewModel(get<AuthenticateRepository>()) }
        viewModel { ProfileViewModel(get<ProfileRepository>(), get<AuthManager>()) }
        viewModel { ChatViewModel(get<AuthManager>(), get<ChatRepository>()) }
        viewModel { AddPostViewModel(get<ActorRepository>(), get<TenorRepository>()) }
    }

expect val platformModule: Module
expect val sqlDriverModule: Module

internal fun getBaseModules() =
    listOf(
        databaseModule,
        networkModule,
        apiModule,
        repositoryModule,
        viewModelModule,
        platformModule,
        sqlDriverModule,
        appModule,
    )

fun initializeKoin(additionalModules: List<Module>) {
    startKoin {
        modules(getBaseModules() + additionalModules)
    }
}
