/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.contexts.cosmic.extensions.RequestResult
import com.contexts.cosmic.ui.composables.ErrorView
import com.contexts.cosmic.ui.composables.FeedItem
import com.contexts.cosmic.ui.composables.Loading
import com.contexts.cosmic.ui.screens.profile.composables.Header
import com.contexts.cosmic.ui.screens.profile.composables.ProfileInfo
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = koinViewModel()
    val profile = viewModel.profile.collectAsState(null)
    val feed = viewModel.feed.collectAsState(RequestResult.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            when (val user = profile.value) {
                is RequestResult.Success -> {
                    item { Header(user.data) }
                    item { ProfileInfo(user.data) }
                }
                else -> Unit
            }
            when (val feedValue = feed.value) {
                is RequestResult.Success -> {
                    items(feedValue.data) { feedItem ->
                        FeedItem(
                            feedItem.post,
                            onReplyClick = {},
                            onRepostClick = {},
                            onLikeClick = {},
                            onMenuClick = {},
                        )
                    }
                }
                is RequestResult.Loading -> {
                    item { Loading() }
                }

                is RequestResult.Error -> {
                    item { ErrorView(feedValue.error.message) }
                }
            }
        }
    }
}
