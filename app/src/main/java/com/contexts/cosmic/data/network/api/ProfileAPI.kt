/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.api

import app.bsky.actor.GetPreferencesResponse
import app.bsky.actor.GetProfileResponse
import app.bsky.actor.PreferencesUnion
import app.bsky.actor.Type
import app.bsky.feed.GetAuthorFeedResponse
import app.bsky.feed.GetFeedGeneratorResponse
import com.contexts.cosmic.data.local.database.entities.FeedEntity
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.data.network.client.safeRequest
import com.contexts.cosmic.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class ProfileAPI(private val client: HttpClient) {
    suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.actor.getProfile")
                parameters.append("actor", actor)
            }
        }
    }

    suspend fun getMyProfile(myDid: String): Response<GetProfileResponse, NetworkError> {
        return getProfile(myDid)
    }

    suspend fun getPreferences(): Response<GetPreferencesResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.actor.getPreferences")
            }
        }
    }

    suspend fun getSavedFeeds(did: String): Response<List<FeedEntity>, NetworkError> {
        try {
            val prefsResponse =
                client.safeRequest<GetPreferencesResponse> {
                    url {
                        method = HttpMethod.Get
                        path("xrpc/app.bsky.actor.getPreferences")
                    }
                }

            return when (prefsResponse) {
                is Response.Success -> {
                    val feedEntities = mutableListOf<FeedEntity>()
                    val savedFeeds =
                        prefsResponse.data.preferences
                            .filterIsInstance<PreferencesUnion.SavedFeedsPrefV2>()
                            .map { it.value.items.filter { item -> item.type is Type.Feed } }
                            .firstOrNull()

                    savedFeeds?.forEach { savedFeed ->
                        val detailsResponse =
                            client.safeRequest<GetFeedGeneratorResponse> {
                                url {
                                    method = HttpMethod.Get
                                    path("xrpc/app.bsky.feed.getFeedGenerator")
                                    parameters.append("feed", savedFeed.value)
                                }
                            }

                        when (detailsResponse) {
                            is Response.Success -> {
                                feedEntities.add(
                                    FeedEntity(
                                        id = savedFeed.id,
                                        userDid = did,
                                        type = savedFeed.type.value,
                                        uri = savedFeed.value,
                                        pinned = savedFeed.pinned,
                                        displayName = detailsResponse.data.view.displayName,
                                    ),
                                )
                            }
                            is Response.Error -> return Response.Error(detailsResponse.error)
                        }
                    }
                    Response.Success(feedEntities)
                }

                is Response.Error -> Response.Error(prefsResponse.error)
            }
        } catch (e: Exception) {
            return Response.Error(NetworkError.Unknown("Unable to get saved feeds"))
        }
    }

    suspend fun getAuthorFeed(
        actor: String,
        limit: Int = 50,
        cursor: String? = null,
    ): Response<GetAuthorFeedResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.feed.getAuthorFeed")
                parameters.append("actor", actor)
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
            }
        }
    }
}
