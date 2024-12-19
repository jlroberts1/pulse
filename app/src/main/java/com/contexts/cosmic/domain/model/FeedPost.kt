/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import app.bsky.embed.ExternalView
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordView
import app.bsky.embed.RecordWithMediaView
import app.bsky.embed.VideoView
import kotlinx.datetime.Instant

data class FeedPost(
    val postUri: String,
    val feedId: String,
    val authorDid: String,
    val authorHandle: String,
    val authorDisplayName: String?,
    val authorAvatar: String?,
    val content: String,
    val indexedAt: Instant,
    val embedType: String?,
    val embedImages: List<ImagesViewImage>?,
    val embedExternal: ExternalView?,
    val embedVideo: VideoView?,
    val embedRecord: RecordView?,
    val embedRecordWithMedia: RecordWithMediaView?,
    val isLiked: Boolean = false,
    val isReposted: Boolean = false,
    val replyCount: Long = 0,
    val repostCount: Long = 0,
    val likeCount: Long = 0,
    val replyParentUri: String? = null,
    val replyRootUri: String? = null,
    val reasonType: String? = null,
    val reasonActorDid: String? = null,
)
