/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.modules

import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.FeedRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.data.repository.UserRepositoryImpl
import com.contexts.cosmic.domain.AuthenticateRepository
import com.contexts.cosmic.domain.FeedRepository
import com.contexts.cosmic.domain.PreferencesRepository
import com.contexts.cosmic.domain.UserRepository
import org.koin.dsl.module

val appModule =
    module {
        single { AuthenticateAPI(get()) }
        single { FeedAPI(get()) }

        single<AuthenticateRepository> { AuthenticateRepositoryImpl(get(), get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<FeedRepository> { FeedRepositoryImpl(get()) }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    }
