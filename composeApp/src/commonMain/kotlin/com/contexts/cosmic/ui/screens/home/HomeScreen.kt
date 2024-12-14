/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.composables.FeedItem
import com.contexts.cosmic.ui.composables.PullToRefreshBox
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(snackbarDelegate: SnackbarDelegate = koinInject()) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = uiState.loading,
        onRefresh = { viewModel.loadFeed() },
    ) {
        uiState.error?.let {
            snackbarDelegate.showSnackbar(
                message = it,
            )
        }

        LazyColumn(
            modifier =
                Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxSize(),
        ) {
            items(uiState.feed) { item ->
                FeedItem(
                    post = item.post,
                    onReplyClick = {},
                    onRepostClick = {},
                    onLikeClick = {},
                    onMenuClick = {},
                )
            }
        }
    }
}
