/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.api

import com.contexts.cosmic.BuildKonfig
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.safeRequest
import com.contexts.cosmic.data.network.httpclient.setupContentNegotiation
import com.contexts.cosmic.data.network.httpclient.setupLogging
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.ui.screens.addpost.composables.TenorResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpHeaders
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
                setupLogging()
            }
        return client.safeRequest {
            url {
                headers.remove(HttpHeaders.Authorization)
                method = HttpMethod.Get
                protocol = URLProtocol.HTTPS
                host = "tenor.googleapis.com"
                path("v2/search")
                parameters.append("q", query)
                parameters.append("key", BuildKonfig.TENOR_API_KEY)
                parameters.append("client_id", "com.contexts.cosmic")
                parameters.append("limit", limit.toString())
            }
        }
    }
}
