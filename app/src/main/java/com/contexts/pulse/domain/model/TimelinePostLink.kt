/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.richtext.Facet
import app.bsky.richtext.FacetFeatureUnion
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle
import sh.christian.ozone.api.Uri

data class TimelinePostLink(
    val start: Int,
    val end: Int,
    val target: LinkTarget,
)

sealed interface LinkTarget {
    data class UserHandleMention(
        val handle: Handle,
    ) : LinkTarget

    data class UserDidMention(
        val did: Did,
    ) : LinkTarget

    data class ExternalLink(
        val uri: Uri,
    ) : LinkTarget

    data class Hashtag(
        val tag: String,
    ) : LinkTarget
}

fun Facet.toLinkOrNull(): TimelinePostLink? {
    return TimelinePostLink(
        start = index.byteStart.toInt(),
        end = index.byteEnd.toInt(),
        target =
            when (val feature = features.first()) {
                is FacetFeatureUnion.Link -> LinkTarget.ExternalLink(feature.value.uri)
                is FacetFeatureUnion.Mention -> LinkTarget.UserDidMention(feature.value.did)
                is FacetFeatureUnion.Tag -> LinkTarget.Hashtag(feature.value.tag)
            },
    )
}
