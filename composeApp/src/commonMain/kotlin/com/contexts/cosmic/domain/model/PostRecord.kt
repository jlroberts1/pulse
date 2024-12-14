/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import app.bsky.feed.ReplyRef
import app.bsky.richtext.Facet
import com.atproto.label.Label
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import sh.christian.ozone.api.model.Timestamp

@Serializable
data class PostRecord(
    val text: String,
    val facets: List<Facet>? = null,
    val reply: ReplyRef? = null,
    val embed: JsonElement? = null,
    val langs: List<String>? = null,
    val labels: List<Label>? = null,
    val createdAt: Timestamp,
)
