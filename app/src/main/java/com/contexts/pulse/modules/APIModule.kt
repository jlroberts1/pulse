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
import io.ktor.client.HttpClient
import org.koin.dsl.module

val apiModule =
    module {
        single<ActorAPI>(createdAtStart = false) { ActorAPI(get<HttpClient>()) }
        single<AuthenticateAPI>(createdAtStart = false) { AuthenticateAPI(get<HttpClient>()) }
        single<ChatAPI>(createdAtStart = false) { ChatAPI(get<HttpClient>()) }
        single<FeedAPI>(createdAtStart = false) { FeedAPI(get<HttpClient>()) }
        single<NotificationsAPI>(createdAtStart = false) { NotificationsAPI(get<HttpClient>()) }
        single<ProfileAPI>(createdAtStart = false) { ProfileAPI(get<HttpClient>()) }
        single<TenorAPI>(createdAtStart = false) { TenorAPI() }
        single<PostAPI>(createdAtStart = false) { PostAPI(get<HttpClient>()) }
        single<UploadAPI>(createdAtStart = false) { UploadAPI(get<HttpClient>()) }
    }
