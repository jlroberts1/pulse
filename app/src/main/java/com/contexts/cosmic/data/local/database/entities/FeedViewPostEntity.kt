/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database.entities

import android.util.Log
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import app.bsky.embed.ExternalView
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordView
import app.bsky.embed.RecordWithMediaView
import app.bsky.embed.VideoView
import app.bsky.feed.FeedViewPost
import app.bsky.feed.FeedViewPostReasonUnion
import app.bsky.feed.PostViewEmbedUnion
import app.bsky.feed.ReplyRefParentUnion
import app.bsky.feed.ReplyRefRootUnion
import com.contexts.cosmic.extensions.getPostText
import kotlinx.datetime.Instant

@Entity(
    tableName = "feed_posts",
    indices = [Index(value = ["feedId"])],
)
data class FeedPostEntity(
    @PrimaryKey
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
) {
    companion object {
        fun from(
            feedViewPost: FeedViewPost,
            feedId: String,
        ): FeedPostEntity {
            val embed = feedViewPost.post.embed

            Log.d("FeedEntity", "Mapping post for feed: $feedId")
            Log.d("FeedEntity", "Post: ${feedViewPost.post.uri}")

            try {
                return FeedPostEntity(
                    postUri = feedViewPost.post.uri.atUri,
                    feedId = feedId,
                    authorDid = feedViewPost.post.author.did.did,
                    authorHandle = feedViewPost.post.author.handle.handle,
                    authorDisplayName = feedViewPost.post.author.displayName,
                    authorAvatar = feedViewPost.post.author.avatar?.uri,
                    content = feedViewPost.post.getPostText(),
                    indexedAt = feedViewPost.post.indexedAt,
                    embedType =
                        when (embed) {
                            is PostViewEmbedUnion.ImagesView -> "images"
                            is PostViewEmbedUnion.ExternalView -> "external"
                            is PostViewEmbedUnion.VideoView -> "video"
                            is PostViewEmbedUnion.RecordView -> "record"
                            is PostViewEmbedUnion.Unknown -> "unknown"
                            is PostViewEmbedUnion.RecordWithMediaView -> "record_with_media"
                            null -> null
                        },
                    embedImages =
                        when (embed) {
                            is PostViewEmbedUnion.ImagesView -> embed.value.images
                            else -> null
                        },
                    embedExternal =
                        when (embed) {
                            is PostViewEmbedUnion.ExternalView -> embed.value
                            else -> null
                        },
                    embedVideo =
                        when (embed) {
                            is PostViewEmbedUnion.VideoView -> embed.value
                            else -> null
                        },
                    embedRecord =
                        when (embed) {
                            is PostViewEmbedUnion.RecordView -> embed.value
                            else -> null
                        },
                    embedRecordWithMedia =
                        when (embed) {
                            is PostViewEmbedUnion.RecordWithMediaView -> embed.value
                            else -> null
                        },
                    isLiked = feedViewPost.post.viewer?.like != null,
                    isReposted = feedViewPost.post.viewer?.repost != null,
                    replyCount = feedViewPost.post.replyCount ?: 0,
                    repostCount = feedViewPost.post.repostCount ?: 0,
                    likeCount = feedViewPost.post.likeCount ?: 0,
                    replyParentUri =
                        when (val parent = feedViewPost.reply?.parent) {
                            is ReplyRefParentUnion.PostView -> parent.value.uri.atUri
                            is ReplyRefParentUnion.BlockedPost -> parent.value.uri.atUri
                            is ReplyRefParentUnion.NotFoundPost -> parent.value.uri.atUri
                            else -> null
                        },
                    replyRootUri =
                        when (val root = feedViewPost.reply?.root) {
                            is ReplyRefRootUnion.PostView -> root.value.uri.atUri
                            is ReplyRefRootUnion.BlockedPost -> root.value.uri.atUri
                            is ReplyRefRootUnion.NotFoundPost -> root.value.uri.atUri
                            else -> null
                        },
                    reasonType =
                        when (feedViewPost.reason) {
                            is FeedViewPostReasonUnion.ReasonRepost -> "repost"
                            is FeedViewPostReasonUnion.ReasonPin -> "pin"
                            is FeedViewPostReasonUnion.Unknown -> "unknown"
                            null -> null
                        },
                    reasonActorDid =
                        when (val reason = feedViewPost.reason) {
                            is FeedViewPostReasonUnion.ReasonRepost -> reason.value.by.did.did
                            else -> null
                        },
                )
            } catch (e: Exception) {
                Log.e("FeedEntity", "Error mapping post: ${e.message}")
                throw e
            }
        }
    }
}
