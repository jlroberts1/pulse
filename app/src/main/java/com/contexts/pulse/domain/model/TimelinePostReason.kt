/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.feed.FeedViewPostReasonUnion
import kotlinx.serialization.Serializable
import sh.christian.ozone.api.model.Timestamp

@Serializable
sealed interface TimelinePostReason {
    data class TimelinePostRepost(
        val repostAuthor: Profile,
        val indexedAt: Timestamp,
    ) : TimelinePostReason

    @Serializable
    data object TimelinePostPin : TimelinePostReason
}

fun FeedViewPostReasonUnion.toReasonOrNull(): TimelinePostReason {
    return when (this) {
        is FeedViewPostReasonUnion.ReasonRepost -> {
            TimelinePostReason.TimelinePostRepost(
                repostAuthor = value.by.toProfile(),
                indexedAt = value.indexedAt,
            )
        }
        is FeedViewPostReasonUnion.ReasonPin -> {
            TimelinePostReason.TimelinePostPin
        }
    }
}
