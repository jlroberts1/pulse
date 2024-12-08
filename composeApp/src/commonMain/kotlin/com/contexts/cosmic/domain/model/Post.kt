package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val uri: String,
    val cid: String,
    val author: Author,
    val record: PostRecord,
    val indexedAt: String,
    val replyCount: Int = 0,
    val repostCount: Int = 0,
    val likeCount: Int = 0
)