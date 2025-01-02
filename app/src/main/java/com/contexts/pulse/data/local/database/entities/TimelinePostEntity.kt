/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.bsky.feed.FeedViewPost
import com.contexts.pulse.domain.model.Label
import com.contexts.pulse.domain.model.Profile
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.TimelinePostFeature
import com.contexts.pulse.domain.model.TimelinePostLink
import com.contexts.pulse.domain.model.TimelinePostReason
import com.contexts.pulse.domain.model.TimelinePostReply
import com.contexts.pulse.domain.model.toPost
import kotlinx.datetime.Instant
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

@Entity(
    tableName = "timeline_posts",
    foreignKeys = [
        ForeignKey(
            entity = FeedEntity::class,
            parentColumns = ["id"],
            childColumns = ["feedId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["feedId"])],
)
data class TimelinePostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val feedId: String,
    val uri: String,
    val cid: String,
    val author: Profile,
    val text: String,
    val textLinks: List<TimelinePostLink>,
    val createdAt: Instant,
    val feature: TimelinePostFeature?,
    val replyCount: Long,
    val repostCount: Long,
    val likeCount: Long,
    val indexedAt: Instant,
    val reposted: Boolean,
    val liked: Boolean,
    val repostedUri: String?,
    val likedUri: String?,
    val labels: List<Label>,
    val reply: TimelinePostReply?,
    val reason: TimelinePostReason?,
    val tags: List<String>,
)

fun TimelinePostEntity.toPost(): TimelinePost {
    return TimelinePost(
        uri = AtUri(uri),
        cid = Cid(cid),
        author = author,
        text = text,
        textLinks = textLinks,
        createdAt = createdAt,
        feature = feature,
        replyCount = replyCount,
        repostCount = repostCount,
        likeCount = likeCount,
        indexedAt = indexedAt,
        repostedUri = repostedUri?.let { AtUri(it) },
        likedUri = likedUri?.let { AtUri(it) },
        reposted = reposted,
        liked = liked,
        labels = labels,
        reply = reply,
        reason = reason,
        tags = tags,
    )
}

fun FeedViewPost.toTimelinePostEntity(feedId: String): TimelinePostEntity {
    val post = this.toPost()
    return TimelinePostEntity(
        feedId = feedId,
        uri = post.uri.atUri,
        cid = post.cid.cid,
        author = post.author,
        text = post.text,
        textLinks = post.textLinks,
        createdAt = post.createdAt,
        feature = post.feature,
        replyCount = post.replyCount,
        repostCount = post.repostCount,
        likeCount = post.likeCount,
        indexedAt = post.indexedAt,
        repostedUri = post.repostedUri?.atUri,
        likedUri = post.likedUri?.atUri,
        reposted = post.reposted,
        liked = post.liked,
        labels = post.labels,
        reply = post.reply,
        reason = post.reason,
        tags = post.tags,
    )
}
