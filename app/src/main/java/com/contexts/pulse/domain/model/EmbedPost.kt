/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import kotlinx.serialization.Serializable
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

@Serializable
sealed interface EmbedPost {
    @Serializable
    data class VisibleEmbedPost(
        val uri: AtUri,
        val cid: Cid,
        val author: Profile,
        val post: LitePost,
    ) : EmbedPost {
        val reference: Reference = Reference(uri, cid)
    }

    @Serializable
    data class FeedGeneratorEmbed(
        val displayName: String,
        val avatar: String?,
        val creator: Profile,
        val likeCount: Long?,
    ) : EmbedPost

    @Serializable
    data class InvisibleEmbedPost(
        val uri: AtUri,
    ) : EmbedPost

    @Serializable
    data class BlockedEmbedPost(
        val uri: AtUri,
    ) : EmbedPost
}
