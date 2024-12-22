/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.bsky.actor.GetSuggestionsResponse
import app.bsky.actor.ProfileView
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import app.bsky.actor.SearchActorsTypeaheadResponse
import com.contexts.pulse.data.network.api.ActorAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import io.ktor.client.HttpClient
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ActorRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val client: HttpClient,
    private val actorAPI: ActorAPI,
) : ActorRepository {
    override suspend fun searchActorsTypeahead(
        searchActorsTypeaheadQueryParams: SearchActorsTypeaheadQueryParams,
    ): Response<SearchActorsTypeaheadResponse, NetworkError> =
        withContext(appDispatchers.io) {
            actorAPI.searchActorsTypeahead(searchActorsTypeaheadQueryParams)
        }

    override fun getSuggestions(): Flow<PagingData<ProfileView>> {
        val request = PagedRequest.SuggestedAccounts()
        return Pager(
            config =
                PagingConfig(
                    pageSize = request.limit,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                NetworkPagingSource<ProfileView, GetSuggestionsResponse>(
                    appDispatchers = appDispatchers,
                    client = client,
                    request = request,
                    type = typeInfo<GetSuggestionsResponse>(),
                    getItems = { it.actors },
                    getCursor = { it.cursor },
                )
            },
        ).flow.flowOn(appDispatchers.io)
    }
}
