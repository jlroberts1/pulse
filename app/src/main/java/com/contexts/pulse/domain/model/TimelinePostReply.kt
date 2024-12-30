/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.feed.ReplyRef
import app.bsky.feed.ReplyRefParentUnion
import app.bsky.feed.ReplyRefRootUnion

data class TimelinePostReply(
    val root: TimelinePost?,
    val parent: TimelinePost?,
)

fun ReplyRef.toReply(): TimelinePostReply {
    return TimelinePostReply(
        root =
            when (val root = root) {
                is ReplyRefRootUnion.BlockedPost -> null
                is ReplyRefRootUnion.NotFoundPost -> null
                is ReplyRefRootUnion.PostView -> root.value.toPost()
            },
        parent =
            when (val parent = parent) {
                is ReplyRefParentUnion.BlockedPost -> null
                is ReplyRefParentUnion.NotFoundPost -> null
                is ReplyRefParentUnion.PostView -> parent.value.toPost()
            },
    )
}
