/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.api

import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.data.network.client.safeRequest
import com.contexts.cosmic.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path

class AuthenticateAPI(private val client: HttpClient) {
    suspend fun createSession(createSessionRequest: CreateSessionRequest): Response<CreateSessionResponse, NetworkError> {
        return client.safeRequest<CreateSessionResponse> {
            url {
                method = HttpMethod.Post
                path("xrpc/com.atproto.server.createSession")
            }
            setBody(createSessionRequest)
        }
    }
}
