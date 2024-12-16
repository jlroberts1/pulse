/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.modules

import com.contexts.cosmic.data.network.api.ActorAPI
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.ChatAPI
import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.network.api.NotificationsAPI
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.api.TenorAPI
import com.contexts.cosmic.data.repository.ActorRepositoryImpl
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.ChatRepositoryImpl
import com.contexts.cosmic.data.repository.FeedRepositoryImpl
import com.contexts.cosmic.data.repository.NotificationsRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.data.repository.ProfileRepositoryImpl
import com.contexts.cosmic.data.repository.TenorRepositoryImpl
import com.contexts.cosmic.data.repository.UserRepositoryImpl
import com.contexts.cosmic.domain.repository.ActorRepository
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.ChatRepository
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.domain.repository.NotificationsRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.domain.repository.TenorRepository
import com.contexts.cosmic.domain.repository.UserRepository
import org.koin.dsl.module

val appModule =
    module {
        single { ActorAPI(get()) }
        single { AuthenticateAPI(get()) }
        single { ChatAPI(get()) }
        single { FeedAPI(get()) }
        single { NotificationsAPI(get()) }
        single { ProfileAPI(get()) }
        single { TenorAPI() }

        single<ActorRepository> { ActorRepositoryImpl(get()) }
        single<AuthenticateRepository> { AuthenticateRepositoryImpl(get(), get(), get()) }
        single<ChatRepository> { ChatRepositoryImpl(get()) }
        single<FeedRepository> { FeedRepositoryImpl(get()) }
        single<NotificationsRepository> { NotificationsRepositoryImpl(get()) }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
        single<ProfileRepository> { ProfileRepositoryImpl(get()) }
        single<TenorRepository> { TenorRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }
