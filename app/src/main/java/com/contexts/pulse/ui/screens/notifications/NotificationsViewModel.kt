/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.notification.ListNotificationsNotification
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.NotificationsRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class NotificationsUiState(
    val notifications: List<ListNotificationsNotification> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

class NotificationViewModel(
    private val notificationsRepository: NotificationsRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun refreshNotifications() {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            notificationsRepository.listNotifications().onSuccess { response ->
                _uiState.update { it.copy(notifications = response.notifications, loading = false) }
                updateSeen()
            }.onError { error ->
                _uiState.update { it.copy(loading = false, error = error.message) }
            }
        }
    }

    private suspend fun updateSeen() {
        notificationsRepository.updateSeen(Clock.System.now()).onSuccess {
            preferencesRepository.updateUnreadCount(0L)
        }
    }
}
