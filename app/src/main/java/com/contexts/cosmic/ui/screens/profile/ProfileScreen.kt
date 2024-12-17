/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contexts.cosmic.ui.composables.FeedItem
import com.contexts.cosmic.ui.composables.PullToRefreshBox
import com.contexts.cosmic.ui.screens.profile.composables.Header
import com.contexts.cosmic.ui.screens.profile.composables.ProfileInfo
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onMediaOpen: (String) -> Unit) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    PullToRefreshBox(
        isRefreshing = uiState.loading,
        onRefresh = { viewModel.refreshProfile() },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            uiState.profile?.let {
                item {
                    Header(
                        it.banner,
                        it.avatar,
                        it.displayName,
                        it.handle.handle,
                    )
                }
                item {
                    ProfileInfo(
                        it.description,
                        it.postsCount,
                        it.followersCount,
                        it.followsCount,
                    )
                }
            }

            if (uiState.feedLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                }
            }
            items(uiState.feed, key = { it.post.uri.atUri }) { item ->
                FeedItem(
                    item.post,
                    onReplyClick = {},
                    onRepostClick = {},
                    onLikeClick = {},
                    onMenuClick = {},
                    onMediaOpen = { onMediaOpen(it) },
                )
            }
        }
    }
}
