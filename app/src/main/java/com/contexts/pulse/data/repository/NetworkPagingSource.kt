/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.contexts.pulse.modules.AppDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.withContext

sealed interface RequestResult<out T> {
    data class Success<out T>(val data: T) : RequestResult<T>

    data object NoCurrentUser : RequestResult<Nothing>
}

sealed class PagedRequest {
    abstract val url: String
    abstract val limit: Int
    abstract val parameters: Map<String, String>

    data class AuthorFeed(
        val actorId: String,
        override val url: String = "xrpc/app.bsky.feed.getAuthorFeed",
        override val limit: Int = 15,
    ) : PagedRequest() {
        override val parameters: Map<String, String> = mapOf("actor" to actorId)
    }

    data class GetFeed(
        val feedUri: String,
        override val url: String = "xrpc/app.bsky.feed.getFeed",
        override val limit: Int = 15,
    ) : PagedRequest() {
        override val parameters: Map<String, String> = mapOf("feed" to feedUri)
    }

    data class SuggestedFeed(
        override val url: String = "xrpc/app.bsky.feed.getSuggestedFeeds",
        override val limit: Int = 15,
        override val parameters: Map<String, String> = emptyMap(),
    ) : PagedRequest()

    data class SuggestedAccounts(
        override val url: String = "xrpc/app.bsky.actor.getSuggestions",
        override val limit: Int = 8,
        override val parameters: Map<String, String> = emptyMap(),
    ) : PagedRequest()
}

class NetworkPagingSource<T : Any, R : Any>(
    private val appDispatchers: AppDispatchers,
    private val client: HttpClient,
    private val request: PagedRequest,
    private val type: TypeInfo,
    private val getItems: (R) -> List<T>,
    private val getCursor: (R) -> String?,
) : PagingSource<String, T>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, T> =
        withContext(appDispatchers.io) {
            try {
                val response =
                    client.get(request.url) {
                        request.parameters.forEach { (key, value) ->
                            parameter(key, value)
                        }

                        params.key?.let { parameter("cursor", it) }
                        parameter("limit", request.limit)
                    }.body<R>(type)

                LoadResult.Page(
                    data = getItems(response),
                    prevKey = null,
                    nextKey = getCursor(response)?.takeIf { it.isNotBlank() },
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<String, T>): String? = null
}
