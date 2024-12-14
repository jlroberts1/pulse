/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.notification.ListNotificationsNotification
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.NotificationsRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _notifications =
        MutableStateFlow<RequestResult<List<ListNotificationsNotification>, AppError>>(RequestResult.Loading)
    val notifications = _notifications.asStateFlow()

    init {
        viewModelScope.launch {
            loadNotifications()
        }
    }

    private suspend fun loadNotifications() {
        _notifications.value = RequestResult.Loading
        when (notificationsRepository.updateSeen(Clock.System.now())) {
            is Response.Success -> {
                preferencesRepository.updateUnreadCount(0L)
                fetchNotifications()
            }

            is Response.Error -> {
                Napier.e("Failed to set notifications as read")
                fetchNotifications()
            }
        }
    }

    private suspend fun fetchNotifications() {
        when (val response = notificationsRepository.listNotifications()) {
            is Response.Success -> {
                _notifications.value = RequestResult.Success(response.data.notifications)
            }

            is Response.Error -> {
                _notifications.value = RequestResult.Error(response.error)
            }
        }
    }
}
