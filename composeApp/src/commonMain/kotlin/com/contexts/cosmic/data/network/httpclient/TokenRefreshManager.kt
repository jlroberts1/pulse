/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.httpclient

import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenRefreshManager {
    private val mutex = Mutex()
    private var isRefreshing = false

    suspend fun executeWithRefresh(
        request: HttpRequestBuilder,
        originalExecute: suspend (HttpRequestBuilder) -> HttpClientCall,
        refresh: suspend () -> Unit,
    ): HttpClientCall {
        val originalCall = originalExecute(request)

        if (originalCall.response.status.value == 400) {
            if (isRefreshing) {
                mutex.withLock {
                    return originalExecute(request)
                }
            }

            return mutex.withLock {
                if (!isRefreshing) {
                    isRefreshing = true
                }
                try {
                    refresh()
                } finally {
                    isRefreshing = false
                }
                originalExecute(request)
            }
        }
        return originalCall
    }
}
