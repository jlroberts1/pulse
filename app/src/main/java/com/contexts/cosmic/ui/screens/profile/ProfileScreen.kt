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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
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
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedScreen =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.loading,
        onRefresh = { viewModel.refreshProfile() },
    ) {
        if (isExpandedScreen) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier =
                        Modifier
                            .width(360.dp)
                            .fillMaxHeight()
                            .padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    uiState.profile?.let { profile ->
                        Header(
                            profile.banner,
                            profile.avatar,
                            profile.displayName,
                            profile.handle.handle,
                        )
                        ProfileInfo(
                            profile.description,
                            profile.postsCount,
                            profile.followersCount,
                            profile.followsCount,
                        )
                    }
                }
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .weight(1f)
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(uiState.feed, key = { it.post.uri.atUri }) { item ->
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .widthIn(max = 840.dp)
                            .fillMaxSize()
                            .weight(1f, fill = false),
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

                    items(uiState.feed, key = { it.post.uri.atUri }) { item ->
                    }
                }
            }
        }
    }
}
