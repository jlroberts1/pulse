/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.ThreadViewPostParentUnion
import app.bsky.feed.ThreadViewPostReplieUnion
import com.contexts.pulse.ui.composables.PostViewItem
import com.contexts.pulse.ui.composables.PostViewReply
import com.contexts.pulse.ui.composables.PullToRefreshBox
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostViewScreen(
    viewModel: PostViewModel = koinViewModel(),
    postId: String,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = uiState.loading,
        onRefresh = { viewModel.getThread() },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThreadView(uiState.threadViewPost)
            OutlinedCard(
                onClick = {
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = "Reply",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Reply to thread...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun ThreadView(
    threadViewPost: GetPostThreadResponseThreadUnion.ThreadViewPost?,
    onPostClick: (String) -> Unit = {},
    onReplyClick: () -> Unit = {},
    onMediaOpen: (String) -> Unit = {},
) {
    if (threadViewPost == null) return
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        threadViewPost.value.parent?.let { parent ->
            item {
                Box(
                    modifier =
                        Modifier
                            .width(2.dp)
                            .heightIn(min = 40.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            .padding(start = 24.dp),
                )

                ElevatedCard(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 8.dp),
                ) {
                    when (parent) {
                        is ThreadViewPostParentUnion.ThreadViewPost -> {
                            PostViewItem(
                                post = parent.value.post,
                                onPostClick = {},
                                onReplyClick = {},
                                onMediaOpen = {},
                                onRepostClick = {},
                                onLikeClick = {},
                                onMenuClick = {},
                            )
                        }

                        is ThreadViewPostParentUnion.BlockedPost,
                        is ThreadViewPostParentUnion.NotFoundPost,
                        is ThreadViewPostParentUnion.Unknown,
                        -> Unit
                    }
                }
            }
        }

        item {
            PostViewItem(
                post = threadViewPost.value.post,
                onPostClick = {},
                onReplyClick = {},
                onMediaOpen = {},
                onRepostClick = {},
                onLikeClick = {},
                onMenuClick = {},
            )
        }

        items(threadViewPost.value.replies) { reply ->
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                when (reply) {
                    is ThreadViewPostReplieUnion.ThreadViewPost -> {
                        PostViewReply(
                            post = reply.value.post,
                            onPostClick = {},
                            onReplyClick = {},
                            onMediaOpen = {},
                            onRepostClick = {},
                            onLikeClick = {},
                            onMenuClick = {},
                        )
                    }

                    is ThreadViewPostReplieUnion.BlockedPost,
                    is ThreadViewPostReplieUnion.NotFoundPost,
                    is ThreadViewPostReplieUnion.Unknown,
                    -> Unit
                }
            }
        }
    }
}
