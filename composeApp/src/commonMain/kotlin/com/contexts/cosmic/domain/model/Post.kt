package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val uri: String,
    val cid: String,
    val author: Author,
    val record: Record,
    val embed: EmbedView? = null,
    val replyCount: Int,
    val repostCount: Int,
    val likeCount: Int,
    val quoteCount: Int,
    val indexedAt: String,
    val viewer: Viewer,
    val labels: List<Label>,
)
