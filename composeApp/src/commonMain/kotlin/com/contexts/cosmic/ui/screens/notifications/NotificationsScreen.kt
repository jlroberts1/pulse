/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contexts.cosmic.extensions.RequestResult
import com.contexts.cosmic.ui.composables.NotificationItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsScreen() {
    val viewModel: NotificationsViewModel = koinViewModel()
    val notificationState by viewModel.notifications.collectAsState()
    when (val notifications = notificationState) {
        is RequestResult.Loading -> {}
        is RequestResult.Error -> {
        }
        is RequestResult.Success -> {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(notifications.data) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}
