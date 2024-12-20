/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import androidx.media3.exoplayer.ExoPlayer
import com.contexts.pulse.data.network.api.ActorAPI
import com.contexts.pulse.data.network.api.AuthenticateAPI
import com.contexts.pulse.data.network.api.ChatAPI
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.api.NotificationsAPI
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.api.TenorAPI
import com.contexts.pulse.data.repository.ActorRepositoryImpl
import com.contexts.pulse.data.repository.AuthenticateRepositoryImpl
import com.contexts.pulse.data.repository.ChatRepositoryImpl
import com.contexts.pulse.data.repository.FeedManager
import com.contexts.pulse.data.repository.FeedRepositoryImpl
import com.contexts.pulse.data.repository.NotificationsRepositoryImpl
import com.contexts.pulse.data.repository.PreferencesRepositoryImpl
import com.contexts.pulse.data.repository.ProfileRepositoryImpl
import com.contexts.pulse.data.repository.TenorRepositoryImpl
import com.contexts.pulse.data.repository.UserRepositoryImpl
import com.contexts.pulse.domain.media.PlayerPoolManager
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.AuthenticateRepository
import com.contexts.pulse.domain.repository.ChatRepository
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.NotificationsRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.TenorRepository
import com.contexts.pulse.domain.repository.UserRepository
import org.koin.dsl.module

val appModule =
    module {
        single<ExoPlayer> { ExoPlayer.Builder(get()).build() }
        single<PlayerPoolManager> { PlayerPoolManager(get()) }
        single { ActorAPI(get()) }
        single { AuthenticateAPI(get()) }
        single { ChatAPI(get()) }
        single { FeedAPI(get()) }
        single { NotificationsAPI(get()) }
        single { ProfileAPI(get()) }
        single { TenorAPI() }
        single { FeedManager(get(), get(), get(), get(), get(), get()) }

        single<ActorRepository> { ActorRepositoryImpl(get(), get()) }
        single<AuthenticateRepository> { AuthenticateRepositoryImpl(get(), get(), get(), get()) }
        single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }
        single<FeedRepository> { FeedRepositoryImpl(get(), get()) }
        single<NotificationsRepository> { NotificationsRepositoryImpl(get()) }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
        single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
        single<TenorRepository> { TenorRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }
