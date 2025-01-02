/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.feed.FeedViewPost
import app.bsky.feed.Post
import app.bsky.feed.PostView
import com.contexts.pulse.utils.deserialize
import kotlinx.serialization.Serializable
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.model.Timestamp

@Serializable
data class TimelinePost(
    val uri: AtUri,
    val cid: Cid,
    val author: Profile,
    val text: String,
    val textLinks: List<TimelinePostLink>,
    val createdAt: Timestamp,
    val feature: TimelinePostFeature?,
    val replyCount: Long,
    val repostCount: Long,
    val likeCount: Long,
    val indexedAt: Timestamp,
    val reposted: Boolean = false,
    val liked: Boolean = false,
    val repostedUri: AtUri?,
    val likedUri: AtUri?,
    val labels: List<Label>,
    val reply: TimelinePostReply?,
    val reason: TimelinePostReason?,
    val tags: List<String>,
)

fun FeedViewPost.toPost(): TimelinePost {
    return post.toPost(
        reply = reply?.toReply(),
        reason = reason?.toReasonOrNull(),
    )
}

fun PostView.toPost(): TimelinePost {
    return toPost(
        reply = null,
        reason = null,
    )
}

fun PostView.toPost(
    reply: TimelinePostReply?,
    reason: TimelinePostReason?,
): TimelinePost {
    val postRecord = Post.serializer().deserialize(record)

    return TimelinePost(
        uri = uri,
        cid = cid,
        author = author.toProfile(),
        text = postRecord.text,
        textLinks = postRecord.facets.mapNotNull { it.toLinkOrNull() },
        createdAt = postRecord.createdAt,
        feature = embed?.toFeature(),
        replyCount = replyCount ?: 0,
        repostCount = repostCount ?: 0,
        likeCount = likeCount ?: 0,
        indexedAt = indexedAt,
        reposted = viewer?.repost != null,
        liked = viewer?.like != null,
        repostedUri = viewer?.repost,
        likedUri = viewer?.like,
        labels = labels.map { it.toLabel() },
        reply = reply,
        reason = reason,
        tags = postRecord.tags,
    )
}
