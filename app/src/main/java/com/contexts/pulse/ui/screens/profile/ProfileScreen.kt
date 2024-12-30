/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.profile

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.window.core.layout.WindowWidthSizeClass
import com.contexts.pulse.ui.composables.FeedItem
import com.contexts.pulse.ui.composables.PullToRefreshBox
import com.contexts.pulse.ui.screens.profile.composables.Header
import com.contexts.pulse.ui.screens.profile.composables.ProfileInfo
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onMediaOpen: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val feed = viewModel.feedState.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedScreen =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.loading,
        onRefresh = { feed.refresh() },
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
                    profile?.let { profile ->
                        Header(
                            profile.banner,
                            profile.avatar,
                            profile.displayName,
                            profile.handle,
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
                    items(
                        count = feed.itemCount,
                        key = feed.itemKey { it.uri.atUri },
                        contentType = feed.itemContentType(),
                    ) { index ->
                        feed[index]?.let { item ->
                            FeedItem(
                                post = item,
                                onPostClick = {},
                                onMediaOpen = { media -> onMediaOpen(media) },
                                onRepostClick = {},
                                onReplyClick = {},
                                onMenuClick = {},
                                onLikeClick = {},
                            )
                        }
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
                    profile?.let {
                        item {
                            Header(
                                it.banner,
                                it.avatar,
                                it.displayName,
                                it.handle,
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

                    items(
                        count = feed.itemCount,
                        key = feed.itemKey { it.uri.atUri },
                        contentType = feed.itemContentType(),
                    ) { index ->
                        feed[index]?.let { item ->
                            FeedItem(
                                post = item,
                                onPostClick = {},
                                onMediaOpen = { media -> onMediaOpen(media) },
                                onRepostClick = {},
                                onReplyClick = {},
                                onMenuClick = {},
                                onLikeClick = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
