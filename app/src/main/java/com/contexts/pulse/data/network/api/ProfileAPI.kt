/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.actor.GetPreferencesResponse
import app.bsky.actor.GetProfileResponse
import app.bsky.actor.PutPreferencesRequest
import com.atproto.repo.CreateRecordResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.data.network.request.CreateFollowRecordRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.path

class ProfileAPI(
    private val client: HttpClient,
) {
    suspend fun getProfile(actor: String): Response<GetProfileResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.actor.getProfile")
                parameters.append("actor", actor)
            }
        }
    }

    suspend fun getPreferences(): Response<GetPreferencesResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.actor.getPreferences")
            }
        }
    }

    suspend fun putPreferences(putPreferencesRequest: PutPreferencesRequest): Response<Unit, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Post
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.actor.putPreferences")
            }
            setBody(putPreferencesRequest)
        }
    }

    suspend fun followUser(createFollowRecordRequest: CreateFollowRecordRequest): Response<CreateRecordResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Post
                host = Url(pdsUrl).host
                path("xrpc/com.atproto.repo.createRecord")
                setBody(createFollowRecordRequest)
            }
        }
    }
}
