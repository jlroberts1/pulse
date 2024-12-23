/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import com.contexts.pulse.data.network.api.ActorAPI
import com.contexts.pulse.data.network.api.AuthenticateAPI
import com.contexts.pulse.data.network.api.ChatAPI
import com.contexts.pulse.data.network.api.FeedAPI
import com.contexts.pulse.data.network.api.NotificationsAPI
import com.contexts.pulse.data.network.api.PostAPI
import com.contexts.pulse.data.network.api.ProfileAPI
import com.contexts.pulse.data.network.api.TenorAPI
import com.contexts.pulse.data.network.api.UploadAPI
import com.contexts.pulse.data.network.client.AccountManager
import io.ktor.client.HttpClient
import org.koin.dsl.module

val apiModule =
    module {
        single { ActorAPI(get<HttpClient>(), get<AccountManager>()) }
        single { AuthenticateAPI(get<HttpClient>()) }
        single { ChatAPI(get<HttpClient>(), get<AccountManager>()) }
        single { FeedAPI(get<HttpClient>(), get<AccountManager>()) }
        single { NotificationsAPI(get<HttpClient>(), get<AccountManager>()) }
        single { ProfileAPI(get<HttpClient>(), get<AccountManager>()) }
        single { TenorAPI() }
        single { PostAPI(get<HttpClient>(), get<AccountManager>()) }
        single { UploadAPI(get<HttpClient>(), get<AccountManager>()) }
    }
