/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import app.bsky.actor.SearchActorsTypeaheadQueryParams
import app.bsky.actor.SearchActorsTypeaheadResponse
import com.contexts.cosmic.data.network.api.ActorAPI
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.domain.repository.ActorRepository
import com.contexts.cosmic.exceptions.NetworkError

class ActorRepositoryImpl(private val actorAPI: ActorAPI) : ActorRepository {
    override suspend fun searchActorsTypeahead(
        searchActorsTypeaheadQueryParams: SearchActorsTypeaheadQueryParams,
    ): Response<SearchActorsTypeaheadResponse, NetworkError> {
        return actorAPI.searchActorsTypeahead(searchActorsTypeaheadQueryParams)
    }
}
