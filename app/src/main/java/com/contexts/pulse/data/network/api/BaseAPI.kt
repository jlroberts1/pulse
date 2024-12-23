/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import com.contexts.pulse.data.network.client.AccountManager
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.Url
import kotlinx.coroutines.flow.first

abstract class BaseAPI(
    protected val client: HttpClient,
    private val accountManager: AccountManager,
) {
    protected suspend fun getPdsUrl(): String {
        return accountManager.currentPdsUrl.first { it != null }
            ?: throw IllegalStateException("No PDS URL available")
    }

    protected suspend fun HttpRequestBuilder.configurePds() {
        url.host = Url(getPdsUrl()).host
    }
}
