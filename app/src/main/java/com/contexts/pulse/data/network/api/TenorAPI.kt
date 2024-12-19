/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import com.contexts.pulse.BuildConfig
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.data.network.client.setupContentNegotiation
import com.contexts.pulse.data.network.response.TenorResponse
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.path

class TenorAPI() {
    suspend fun searchTenor(
        query: String,
        limit: Int = 10,
    ): Response<TenorResponse, NetworkError> {
        val client =
            HttpClient {
                expectSuccess = true
                setupContentNegotiation()
            }
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                protocol = URLProtocol.HTTPS
                host = "tenor.googleapis.com"
                path("v2/search")
                parameters.append("q", query)
                parameters.append("key", BuildConfig.TENOR_API_KEY)
                parameters.append("client_id", "com.contexts.pulse")
                parameters.append("limit", limit.toString())
            }
        }
    }
}
