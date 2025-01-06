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
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.network.api.AuthenticateAPI
import com.contexts.pulse.data.network.api.ChatAPI
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.api.NotificationsAPI
import com.contexts.pulse.data.network.api.PostAPI
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.api.TenorAPI
import com.contexts.pulse.data.network.api.UploadAPI
import com.contexts.pulse.data.repository.ActorRepositoryImpl
import com.contexts.pulse.data.repository.AuthenticateRepositoryImpl
import com.contexts.pulse.data.repository.ChatRepositoryImpl
import com.contexts.pulse.data.repository.FeedRepositoryImpl
import com.contexts.pulse.data.repository.NotificationsRepositoryImpl
import com.contexts.pulse.data.repository.PendingUploadRepositoryImpl
import com.contexts.pulse.data.repository.PostRepositoryImpl
import com.contexts.pulse.data.repository.PreferencesRepositoryImpl
import com.contexts.pulse.data.repository.ProfileRepositoryImpl
import com.contexts.pulse.data.repository.TenorRepositoryImpl
import com.contexts.pulse.data.repository.TimelineManager
import com.contexts.pulse.data.repository.TimelineRemoteMediatorFactory
import com.contexts.pulse.data.repository.UserRepositoryImpl
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.AuthenticateRepository
import com.contexts.pulse.domain.repository.ChatRepository
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.NotificationsRepository
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.TenorRepository
import com.contexts.pulse.domain.repository.UserRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module

val repositoryModule =
    module {
        single<ActorRepository>(createdAtStart = false) {
            ActorRepositoryImpl(
                get<AppDispatchers>(),
                get<HttpClient>(),
                get(),
            )
        }
        single<AuthenticateRepository>(createdAtStart = false) {
            AuthenticateRepositoryImpl(
                get<AppDispatchers>(),
                get<AuthenticateAPI>(),
                get<UserRepository>(),
                get<PreferencesRepository>(),
                get<ProfileRepository>(),
            )
        }
        single<ChatRepository>(createdAtStart = false) {
            ChatRepositoryImpl(
                get<AppDispatchers>(),
                get<ChatAPI>(),
            )
        }
        single<FeedRepository>(createdAtStart = false) {
            FeedRepositoryImpl(
                get<AppDispatchers>(),
                get<HttpClient>(),
                get<FeedAPI>(),
                get<PulseDatabase>(),
            )
        }
        single<NotificationsRepository>(createdAtStart = false) {
            NotificationsRepositoryImpl(
                get<AppDispatchers>(),
                get<NotificationsAPI>(),
            )
        }
        single<PreferencesRepository>(createdAtStart = false) {
            PreferencesRepositoryImpl(
                get<AppDispatchers>(),
                get<DataStore<Preferences>>(),
            )
        }
        single<ProfileRepository>(createdAtStart = false) {
            ProfileRepositoryImpl(
                get<AppDispatchers>(),
                get<FeedAPI>(),
                get<HttpClient>(),
                get<PreferencesRepository>(),
                get<ProfileAPI>(),
                get<PulseDatabase>(),
            )
        }
        single<TenorRepository>(createdAtStart = false) {
            TenorRepositoryImpl(
                get<AppDispatchers>(),
                get<TenorAPI>(),
            )
        }
        single<UserRepository>(createdAtStart = false) {
            UserRepositoryImpl(
                get<AppDispatchers>(),
                get<PulseDatabase>(),
            )
        }
        single<PostRepository>(createdAtStart = false) {
            PostRepositoryImpl(
                get<AppDispatchers>(),
                get<PostAPI>(),
                get<UploadAPI>(),
            )
        }
        single<PendingUploadRepository>(createdAtStart = false) {
            PendingUploadRepositoryImpl(
                get<AppDispatchers>(),
                get<UploadAPI>(),
                get<PulseDatabase>(),
            )
        }
        single<TimelineManager>(createdAtStart = false) {
            TimelineManager(
                get<AppDispatchers>(),
                get<TimelineRemoteMediatorFactory>(),
                get<PulseDatabase>(),
            )
        }
        single<TimelineRemoteMediatorFactory>(createdAtStart = false) {
            TimelineRemoteMediatorFactory(
                get<AppDispatchers>(),
                get<FeedRepository>(),
                get<PulseDatabase>(),
            )
        }
    }
