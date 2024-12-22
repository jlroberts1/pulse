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
import io.ktor.client.HttpClient
import org.koin.dsl.module

val apiModule =
    module {
        single { ActorAPI(get<HttpClient>()) }
        single { AuthenticateAPI(get<HttpClient>()) }
        single { ChatAPI(get<HttpClient>()) }
        single { FeedAPI(get<HttpClient>()) }
        single { NotificationsAPI(get<HttpClient>()) }
        single { ProfileAPI(get<HttpClient>()) }
        single { TenorAPI() }
        single { PostAPI(get<HttpClient>()) }
    }
