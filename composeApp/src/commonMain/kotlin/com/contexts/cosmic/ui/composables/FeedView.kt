/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import app.bsky.feed.FeedViewPost
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.extensions.RequestResult

@Composable
fun FeedView(feed: RequestResult<List<FeedViewPost>, AppError>) {
    LazyColumn {
        when (feed) {
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
