/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contexts.pulse.domain.model.Thread
import com.contexts.pulse.domain.model.ThreadPost
import com.contexts.pulse.ui.composables.PullToRefreshBox
import com.contexts.pulse.ui.composables.ScaffoldedScreen
import com.contexts.pulse.ui.screens.postview.composables.PostViewItem
import com.contexts.pulse.ui.screens.postview.composables.PostViewReply
import com.contexts.pulse.ui.screens.postview.composables.ReplyField
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostViewScreen(
    viewModel: PostViewModel = koinViewModel(),
    postId: String,
    navController: NavController,
    drawerState: DrawerState,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScaffoldedScreen(
        navController = navController,
        title = "Post",
        drawerState = drawerState,
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = uiState.loading,
            onRefresh = { viewModel.getThread() },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ThreadView(uiState.thread)
                ReplyField(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter),
                    onClick = {},
                )
            }
        }
    }
}

@Composable
fun ThreadView(
    thread: Thread?,
    onPostClick: (String) -> Unit = {},
    onReplyClick: () -> Unit = {},
    onMediaOpen: (String) -> Unit = {},
) {
    if (thread == null) return
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 56.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(thread.parents) { parent ->
            when (parent) {
                ThreadPost.BlockedPost -> TODO()
                ThreadPost.NotFoundPost -> TODO()
                is ThreadPost.ViewablePost -> {
                    PostViewItem(
                        post = parent.post,
                        onPostClick = {},
                        onReplyClick = {},
                        onMediaOpen = {},
                        onRepostClick = {},
                        onLikeClick = {},
                        onMenuClick = {},
                    )
                }
            }
        }
        item {
            PostViewItem(
                post = thread.post,
                onPostClick = {},
                onReplyClick = {},
                onMediaOpen = {},
                onRepostClick = {},
                onLikeClick = {},
                onMenuClick = {},
            )
        }
        items(thread.replies) { reply ->
            ElevatedCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp),
            ) {
                when (reply) {
                    is ThreadPost.ViewablePost -> {
                        PostViewReply(
                            post = reply.post,
                            onPostClick = {},
                            onReplyClick = {},
                            onMediaOpen = {},
                            onRepostClick = {},
                            onLikeClick = {},
                            onMenuClick = {},
                        )
                    }

                    ThreadPost.BlockedPost,
                    ThreadPost.NotFoundPost,
                    -> Unit
                }
            }
        }
    }
}
