/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.feed.GetPostThreadResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class PostAPI(private val client: HttpClient) {
    suspend fun getPostThread(uri: String): Response<GetPostThreadResponse, NetworkError> {
        return client.safeRequest<GetPostThreadResponse> {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getPostThread")
                parameters.append("uri", uri)
            }
        }
    }
}
