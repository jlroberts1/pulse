/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.feed.Post
import kotlinx.serialization.Serializable
import sh.christian.ozone.api.model.Timestamp

@Serializable
data class LitePost(
    val text: String,
    val links: List<TimelinePostLink>,
    val createdAt: Timestamp,
)

fun Post.toLitePost(): LitePost {
    return LitePost(
        text = text,
        links = facets.mapNotNull { it.toLinkOrNull() },
        createdAt = createdAt,
    )
}
