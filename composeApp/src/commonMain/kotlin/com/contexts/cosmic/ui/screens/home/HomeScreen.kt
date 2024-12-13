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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.contexts.cosmic.extensions.RequestResult
import com.contexts.cosmic.ui.composables.ErrorView
import com.contexts.cosmic.ui.composables.FeedItem
import com.contexts.cosmic.ui.composables.Loading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinViewModel()
    val feedState = viewModel.feed.collectAsState(RequestResult.Loading)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val feed = feedState.value) {
            is RequestResult.Loading -> {
                item { Loading() }
            }

            is RequestResult.Error -> {
                item { ErrorView(feed.error.message) }
            }

            is RequestResult.Success -> {
                items(feed.data) { item ->
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
}
