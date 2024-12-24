/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.notification.GetUnreadCountResponse
import app.bsky.notification.ListNotificationsResponse
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.data.network.response.GenericResponse
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.path
import sh.christian.ozone.api.model.Timestamp

class NotificationsAPI(
    private val client: HttpClient,
) {
    suspend fun listNotifications(
        limit: Int = 50,
        cursor: String? = null,
        priority: Boolean? = false,
    ): Response<ListNotificationsResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.notification.listNotifications")
                parameters.append("limit", limit.toString())
                cursor?.let { parameters.append("cursor", it) }
                priority?.let { parameters.append("priority", it.toString()) }
            }
        }
    }

    suspend fun updateSeen(seenAt: Timestamp): Response<GenericResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Post
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.notification.updateSeen")
                setBody(mapOf("seenAt" to seenAt))
            }
        }
    }

    suspend fun getUnreadCount(): Response<GetUnreadCountResponse, NetworkError> {
        val pdsUrl = UrlManager.getPdsUrl()
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                host = Url(pdsUrl).host
                path("xrpc/app.bsky.notification.getUnreadCount")
            }
        }
    }
}
