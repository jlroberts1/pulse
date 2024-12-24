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
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.dao.PendingUploadDao
import com.contexts.pulse.data.local.database.dao.ProfileDao
import com.contexts.pulse.data.local.database.dao.UserDao
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
        single<ActorRepository> {
            ActorRepositoryImpl(
                get<AppDispatchers>(),
                get<HttpClient>(),
                get(),
            )
        }
        single<AuthenticateRepository> {
            AuthenticateRepositoryImpl(
                get<AppDispatchers>(),
                get<AuthenticateAPI>(),
                get<UserRepository>(),
                get<PreferencesRepository>(),
                get<ProfileRepository>(),
            )
        }
        single<ChatRepository> {
            ChatRepositoryImpl(
                get<AppDispatchers>(),
                get<ChatAPI>(),
            )
        }
        single<FeedRepository> {
            FeedRepositoryImpl(
                get<AppDispatchers>(),
                get<HttpClient>(),
                get<FeedAPI>(),
                get<FeedDao>(),
                get<ProfileAPI>(),
            )
        }
        single<NotificationsRepository> {
            NotificationsRepositoryImpl(
                get<AppDispatchers>(),
                get<NotificationsAPI>(),
            )
        }
        single<PreferencesRepository> {
            PreferencesRepositoryImpl(
                get<AppDispatchers>(),
                get<DataStore<Preferences>>(),
            )
        }
        single<ProfileRepository> {
            ProfileRepositoryImpl(
                get<AppDispatchers>(),
                get<HttpClient>(),
                get<ProfileAPI>(),
                get<ProfileDao>(),
                get<PreferencesRepository>(),
            )
        }
        single<TenorRepository> {
            TenorRepositoryImpl(
                get<AppDispatchers>(),
                get<TenorAPI>(),
            )
        }
        single<UserRepository> {
            UserRepositoryImpl(
                get<AppDispatchers>(),
                get<UserDao>(),
            )
        }
        single<PostRepository> {
            PostRepositoryImpl(
                get<AppDispatchers>(),
                get<PostAPI>(),
            )
        }
        single<PendingUploadRepository> {
            PendingUploadRepositoryImpl(
                get<AppDispatchers>(),
                get<PendingUploadDao>(),
                get<UploadAPI>(),
            )
        }
    }
