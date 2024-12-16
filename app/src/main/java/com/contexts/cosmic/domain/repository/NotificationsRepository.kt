/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.repository

import app.bsky.notification.GetUnreadCountResponse
import app.bsky.notification.ListNotificationsResponse
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.data.network.response.GenericResponse
import com.contexts.cosmic.exceptions.NetworkError
import sh.christian.ozone.api.model.Timestamp

interface NotificationsRepository {
    suspend fun listNotifications(): Response<ListNotificationsResponse, NetworkError>

    suspend fun getUnreadCount(): Response<GetUnreadCountResponse, NetworkError>

    suspend fun updateSeen(seenAt: Timestamp): Response<GenericResponse, NetworkError>
}
