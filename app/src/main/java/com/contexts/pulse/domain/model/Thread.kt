/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.feed.ThreadViewPost
import app.bsky.feed.ThreadViewPostParentUnion
import app.bsky.feed.ThreadViewPostReplieUnion

data class Thread(
    val post: TimelinePost,
    val parents: List<ThreadPost>,
    val replies: List<ThreadPost>,
)

sealed interface ThreadPost {
    data class ViewablePost(
        val post: TimelinePost,
        val replies: List<ThreadPost>,
    ) : ThreadPost

    object NotFoundPost : ThreadPost

    object BlockedPost : ThreadPost
}

fun ThreadViewPost.toThread(): Thread {
    return Thread(
        post = post.toPost(),
        parents =
            generateSequence(parent) { parentPost ->
                when (parentPost) {
                    is ThreadViewPostParentUnion.BlockedPost -> null
                    is ThreadViewPostParentUnion.NotFoundPost -> null
                    is ThreadViewPostParentUnion.ThreadViewPost -> parentPost.value.parent
                }
            }
                .map { it.toThreadPost() }
                .toList()
                .reversed(),
        replies = replies.map { reply -> reply.toThreadPost() },
    )
}

fun ThreadViewPostParentUnion.toThreadPost(): ThreadPost =
    when (this) {
        is ThreadViewPostParentUnion.ThreadViewPost ->
            ThreadPost.ViewablePost(
                post = value.post.toPost(),
                replies = value.replies.map { it.toThreadPost() },
            )
        is ThreadViewPostParentUnion.NotFoundPost -> ThreadPost.NotFoundPost
        is ThreadViewPostParentUnion.BlockedPost -> ThreadPost.BlockedPost
    }

fun ThreadViewPostReplieUnion.toThreadPost(): ThreadPost =
    when (this) {
        is ThreadViewPostReplieUnion.ThreadViewPost ->
            ThreadPost.ViewablePost(
                post = value.post.toPost(),
                replies = value.replies.map { it.toThreadPost() },
            )
        is ThreadViewPostReplieUnion.NotFoundPost -> ThreadPost.NotFoundPost
        is ThreadViewPostReplieUnion.BlockedPost -> ThreadPost.BlockedPost
    }
