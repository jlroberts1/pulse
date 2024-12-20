/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import androidx.paging.PagingData
import app.bsky.actor.ProfileView
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import app.bsky.actor.SearchActorsTypeaheadResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.exceptions.NetworkError
import kotlinx.coroutines.flow.Flow

interface ActorRepository {
    suspend fun searchActorsTypeahead(
        searchActorsTypeaheadQueryParams: SearchActorsTypeaheadQueryParams,
    ): Response<SearchActorsTypeaheadResponse, NetworkError>

    fun getSuggestions(): Flow<PagingData<ProfileView>>
}
