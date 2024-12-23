/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.actor.SearchActorsTypeaheadQueryParams
import app.bsky.actor.SearchActorsTypeaheadResponse
import com.contexts.pulse.data.network.client.AccountManager
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class ActorAPI(
    client: HttpClient,
    accountManager: AccountManager,
) : BaseAPI(client, accountManager) {
    suspend fun searchActorsTypeahead(
        searchActorsTypeaheadQueryParams: SearchActorsTypeaheadQueryParams,
    ): Response<SearchActorsTypeaheadResponse, NetworkError> {
        return client.safeRequest {
            configurePds()
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.actor.searchActorsTypeahead")
                parameters.append("q", searchActorsTypeaheadQueryParams.q.toString())
            }
        }
    }
}
