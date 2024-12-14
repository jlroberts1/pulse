/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.bsky.feed.PostView
import com.contexts.cosmic.extensions.getPostText

@Composable
fun FeedItem(
    post: PostView,
    onReplyClick: () -> Unit,
    onRepostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.padding(top = 8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        ) {
            PostHeader(
                avatar = post.author.avatar,
                displayName = post.author.displayName,
                handle = post.author.handle.handle,
                indexedAt = post.indexedAt,
            )
            PostMessageText(post.getPostText())
            post.embed?.let { EmbedView(it) }
            FeedItemInteractions(
                replyCount = post.replyCount,
                repostCount = post.replyCount,
                likeCount = post.likeCount,
            )
        }
    }
}
