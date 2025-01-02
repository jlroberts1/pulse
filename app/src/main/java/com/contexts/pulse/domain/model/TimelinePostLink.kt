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
import kotlinx.serialization.Serializable
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle
import sh.christian.ozone.api.Uri

@Serializable
data class TimelinePostLink(
    val start: Int,
    val end: Int,
    val target: LinkTarget,
)

@Serializable
sealed interface LinkTarget {
    @Serializable
    data class UserHandleMention(
        val handle: Handle,
    ) : LinkTarget

    @Serializable
    data class UserDidMention(
        val did: Did,
    ) : LinkTarget

    @Serializable
    data class ExternalLink(
        val uri: Uri,
    ) : LinkTarget

    @Serializable
    data class Hashtag(
        val tag: String,
    ) : LinkTarget
}

fun Facet.toLinkOrNull(): TimelinePostLink {
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
