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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.contexts.pulse.domain.model.Thread
import com.contexts.pulse.domain.model.ThreadPost
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.ui.composables.PullToRefreshBox
import com.contexts.pulse.ui.composables.ScaffoldedScreen
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import com.contexts.pulse.ui.screens.postview.composables.PostViewItem
import com.contexts.pulse.ui.screens.postview.composables.PostViewReply
import com.contexts.pulse.ui.screens.postview.composables.ReplyField
import io.ktor.http.encodeURLParameter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostViewScreen(
    viewModel: PostViewModel = koinViewModel(),
    postId: String,
    navController: NavController,
    drawerState: DrawerState,
    onMediaOpen: (String) -> Unit,
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
                ThreadView(
                    thread = uiState.thread,
                    navController = navController,
                    onLikeClick = { viewModel.onLikeClicked(it) },
                    onMediaOpen = { onMediaOpen(it) },
                )
                ReplyField(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter),
                    onClick = {
                        uiState.thread?.post?.uri?.atUri?.encodeURLParameter()?.let {
                            navController.navigate(
                                NavigationRoutes.Authenticated.AddPost.createRoute(it),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun ThreadView(
    thread: Thread?,
    navController: NavController,
    onLikeClick: (TimelinePost) -> Unit,
    onMediaOpen: (String) -> Unit = {},
) {
    if (thread == null) return
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 56.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(thread.parents) { parent ->
            when (parent) {
                ThreadPost.BlockedPost -> TODO()
                ThreadPost.NotFoundPost -> TODO()
                is ThreadPost.ViewablePost -> {
                    PostViewItem(
                        post = parent.post,
                        onPostClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.PostView.createRoute(it.encodeURLParameter()),
                            )
                        },
                        onReplyClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.AddPost.createRoute(it.encodeURLParameter()),
                            )
                        },
                        onMediaOpen = { onMediaOpen(it) },
                        onRepostClick = {},
                        onLikeClick = { onLikeClick(parent.post) },
                        onMenuClick = {},
                        onProfileClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.ViewProfile.createRoute(parent.post.author.did.did),
                            )
                        },
                    )
                }
            }
        }

        val lastParent = thread.parents.filterIsInstance<ThreadPost.ViewablePost>().lastOrNull()
        if (lastParent != null) {
            item {
                PostViewReply(
                    post = thread.post,
                    parent = lastParent.post,
                    onPostClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.PostView.createRoute(it.encodeURLParameter()),
                        )
                    },
                    onReplyClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.AddPost.createRoute(it.encodeURLParameter()),
                        )
                    },
                    onMediaOpen = { onMediaOpen(it) },
                    onRepostClick = {},
                    onLikeClick = { onLikeClick(thread.post) },
                    onMenuClick = {},
                    onProfileClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.ViewProfile.createRoute(thread.post.author.did.did),
                        )
                    },
                )
            }
        } else {
            item {
                PostViewItem(
                    post = thread.post,
                    onPostClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.PostView.createRoute(it.encodeURLParameter()),
                        )
                    },
                    onReplyClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.AddPost.createRoute(it.encodeURLParameter()),
                        )
                    },
                    onMediaOpen = { onMediaOpen(it) },
                    onRepostClick = {},
                    onLikeClick = { onLikeClick(thread.post) },
                    onMenuClick = {},
                    onProfileClick = {
                        navController.navigate(
                            NavigationRoutes.Authenticated.ViewProfile.createRoute(thread.post.author.did.did),
                        )
                    },
                )
            }
        }
        items(thread.replies) { reply ->
            when (reply) {
                is ThreadPost.ViewablePost -> {
                    PostViewReply(
                        post = reply.post,
                        parent = thread.post,
                        onPostClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.PostView.createRoute(reply.post.uri.atUri.encodeURLParameter()),
                            )
                        },
                        onReplyClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.AddPost.createRoute(it.encodeURLParameter()),
                            )
                        },
                        onMediaOpen = { onMediaOpen(it) },
                        onRepostClick = {},
                        onLikeClick = { onLikeClick(reply.post) },
                        onMenuClick = {},
                        onProfileClick = {
                            navController.navigate(
                                NavigationRoutes.Authenticated.ViewProfile.createRoute(reply.post.author.did.did),
                            )
                        },
                    )
                }

                ThreadPost.BlockedPost,
                ThreadPost.NotFoundPost,
                -> Unit
            }
        }
    }
}
