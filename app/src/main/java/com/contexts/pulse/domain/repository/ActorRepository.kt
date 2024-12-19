/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import app.bsky.actor.GetSuggestionsQueryParams
import app.bsky.actor.GetSuggestionsResponse
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import app.bsky.actor.SearchActorsTypeaheadResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.exceptions.NetworkError

interface ActorRepository {
    suspend fun searchActorsTypeahead(
        searchActorsTypeaheadQueryParams: SearchActorsTypeaheadQueryParams,
    ): Response<SearchActorsTypeaheadResponse, NetworkError>

    suspend fun getSuggestions(getSuggestionsQueryParams: GetSuggestionsQueryParams): Response<GetSuggestionsResponse, NetworkError>
}
