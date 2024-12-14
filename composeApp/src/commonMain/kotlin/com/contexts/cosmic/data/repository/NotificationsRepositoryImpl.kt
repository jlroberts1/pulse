/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import app.bsky.notification.GetUnreadCountResponse
import app.bsky.notification.ListNotificationsResponse
import com.contexts.cosmic.data.network.api.NotificationsAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.GenericResponse
import com.contexts.cosmic.domain.repository.NotificationsRepository
import com.contexts.cosmic.exceptions.NetworkError
import sh.christian.ozone.api.model.Timestamp

class NotificationsRepositoryImpl(private val notificationsAPI: NotificationsAPI) : NotificationsRepository {
    override suspend fun listNotifications(): Response<ListNotificationsResponse, NetworkError> = notificationsAPI.listNotifications()

    override suspend fun getUnreadCount(): Response<GetUnreadCountResponse, NetworkError> = notificationsAPI.getUnreadCount()

    override suspend fun updateSeen(seenAt: Timestamp): Response<GenericResponse, NetworkError> = notificationsAPI.updateSeen(seenAt)
}
