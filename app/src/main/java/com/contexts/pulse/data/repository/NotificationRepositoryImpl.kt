/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import app.bsky.notification.GetUnreadCountResponse
import app.bsky.notification.ListNotificationsResponse
import com.contexts.pulse.data.network.api.NotificationsAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.response.GenericResponse
import com.contexts.pulse.domain.repository.NotificationsRepository
import com.contexts.pulse.exceptions.NetworkError
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.withContext
import sh.christian.ozone.api.model.Timestamp

class NotificationsRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val notificationsAPI: NotificationsAPI,
) : NotificationsRepository {
    override suspend fun listNotifications(): Response<ListNotificationsResponse, NetworkError> =
        withContext(appDispatchers.io) {
            notificationsAPI.listNotifications()
        }

    override suspend fun getUnreadCount(): Response<GetUnreadCountResponse, NetworkError> =
        withContext(appDispatchers.io) {
            notificationsAPI.getUnreadCount()
        }

    override suspend fun updateSeen(seenAt: Timestamp): Response<GenericResponse, NetworkError> =
        withContext(appDispatchers.io) {
            notificationsAPI.updateSeen(seenAt)
        }
}
