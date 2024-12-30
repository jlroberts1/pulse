/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.embed.AspectRatio
import app.bsky.embed.ExternalView
import app.bsky.embed.ImagesView
import app.bsky.embed.RecordViewRecordUnion
import app.bsky.embed.RecordWithMediaViewMediaUnion
import app.bsky.embed.VideoView
import app.bsky.feed.Post
import app.bsky.feed.PostViewEmbedUnion
import com.contexts.pulse.utils.deserialize
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Uri

sealed interface TimelinePostFeature {
    data class ImagesFeature(
        val images: List<EmbedImage>,
    ) : TimelinePostFeature, TimelinePostMedia

    data class VideoFeature(
        val video: EmbedVideo,
    ) : TimelinePostFeature, TimelinePostMedia

    data class ExternalFeature(
        val uri: Uri,
        val title: String,
        val description: String,
        val thumb: String?,
    ) : TimelinePostFeature, TimelinePostMedia

    data class PostFeature(
        val post: EmbedPost,
    ) : TimelinePostFeature

    data class MediaPostFeature(
        val post: EmbedPost,
        val media: TimelinePostMedia,
    ) : TimelinePostFeature
}

sealed interface TimelinePostMedia

data class EmbedImage(
    val thumb: String,
    val fullsize: String,
    val aspectRatio: AspectRatio?,
    val alt: String,
)

data class EmbedVideo(
    val thumb: String?,
    val playlist: String,
    val aspectRatio: AspectRatio?,
)

sealed interface EmbedPost {
    data class VisibleEmbedPost(
        val uri: AtUri,
        val cid: Cid,
        val author: Profile,
        val post: LitePost,
    ) : EmbedPost {
        val reference: Reference = Reference(uri, cid)
    }

    data class FeedGeneratorEmbed(
        val displayName: String,
        val avatar: String?,
        val creator: Profile,
        val likeCount: Long?,
    ) : EmbedPost

    data class InvisibleEmbedPost(
        val uri: AtUri,
    ) : EmbedPost

    data class BlockedEmbedPost(
        val uri: AtUri,
    ) : EmbedPost
}

fun PostViewEmbedUnion.toFeature(): TimelinePostFeature? {
    return when (this) {
        is PostViewEmbedUnion.ImagesView -> {
            value.toImagesFeature()
        }
        is PostViewEmbedUnion.VideoView -> {
            value.toVideoFeature()
        }
        is PostViewEmbedUnion.ExternalView -> {
            value.toExternalFeature()
        }
        is PostViewEmbedUnion.RecordView -> {
            TimelinePostFeature.PostFeature(
                post = value.record.toEmbedPost(),
            )
        }
        is PostViewEmbedUnion.RecordWithMediaView -> {
            TimelinePostFeature.MediaPostFeature(
                post = value.record.record.toEmbedPost(),
                media =
                    when (val media = value.media) {
                        is RecordWithMediaViewMediaUnion.ExternalView -> media.value.toExternalFeature()
                        is RecordWithMediaViewMediaUnion.ImagesView -> media.value.toImagesFeature()
                        is RecordWithMediaViewMediaUnion.VideoView -> media.value.toVideoFeature()
                    },
            )
        }
    }
}

private fun ImagesView.toImagesFeature(): TimelinePostFeature.ImagesFeature {
    return TimelinePostFeature.ImagesFeature(
        images =
            images.map {
                EmbedImage(
                    thumb = it.thumb.uri,
                    fullsize = it.fullsize.uri,
                    aspectRatio = it.aspectRatio,
                    alt = it.alt,
                )
            },
    )
}

private fun VideoView.toVideoFeature(): TimelinePostFeature.VideoFeature {
    return TimelinePostFeature.VideoFeature(
        video =
            EmbedVideo(
                thumb = thumbnail?.uri,
                playlist = playlist.uri,
                aspectRatio = aspectRatio,
            ),
    )
}

private fun ExternalView.toExternalFeature(): TimelinePostFeature.ExternalFeature {
    return TimelinePostFeature.ExternalFeature(
        uri = external.uri,
        title = external.title,
        description = external.description,
        thumb = external.thumb?.uri,
    )
}

private fun RecordViewRecordUnion.toEmbedPost(): EmbedPost {
    return when (this) {
        is RecordViewRecordUnion.ViewBlocked -> {
            EmbedPost.BlockedEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.ViewNotFound -> {
            EmbedPost.InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.ViewRecord -> {
            val post = Post.serializer().deserialize(value.value).toLitePost()

            EmbedPost.VisibleEmbedPost(
                uri = value.uri,
                cid = value.cid,
                author = value.author.toProfile(),
                post = post,
            )
        }
        is RecordViewRecordUnion.FeedGeneratorView -> {
            EmbedPost.FeedGeneratorEmbed(
                creator = value.creator.toProfile(),
                displayName = value.displayName,
                avatar = value.avatar?.uri,
                likeCount = value.likeCount,
            )
        }
        is RecordViewRecordUnion.GraphListView -> {
            EmbedPost.InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.LabelerLabelerView -> {
            EmbedPost.InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.GraphStarterPackViewBasic -> {
            EmbedPost.InvisibleEmbedPost(
                uri = value.uri,
            )
        }
        is RecordViewRecordUnion.ViewDetached -> {
            EmbedPost.InvisibleEmbedPost(
                uri = value.uri,
            )
        }
    }
}
