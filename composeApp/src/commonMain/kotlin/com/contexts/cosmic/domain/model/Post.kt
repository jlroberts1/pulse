package com.contexts.cosmic.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("\$type")
    val type: String = "app.bsky.feed.post",
    val uri: String,
    val cid: String,
    val author: Author,
    val record: Record,
    val embed: EmbedView? = null,
    val langs: List<String>? = null,
    val replyCount: Int,
    val repostCount: Int,
    val likeCount: Int,
    val quoteCount: Int,
    val indexedAt: String,
    val viewer: Viewer,
    val labels: List<Label>,
)
