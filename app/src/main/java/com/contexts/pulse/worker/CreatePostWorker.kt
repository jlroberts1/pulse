/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.bsky.richtext.Facet
import app.bsky.richtext.FacetByteSlice
import app.bsky.richtext.FacetFeatureUnion
import app.bsky.richtext.FacetLink
import app.bsky.richtext.FacetMention
import com.contexts.pulse.R
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.network.client.OzoneJsonConfig
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.model.CreateRecord
import com.contexts.pulse.domain.model.ImageEmbed
import com.contexts.pulse.domain.model.ImageRef
import com.contexts.pulse.domain.model.PostEmbed
import com.contexts.pulse.domain.model.PostRecord
import com.contexts.pulse.domain.model.VideoEmbed
import com.contexts.pulse.domain.model.VideoRef
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.Uri
import sh.christian.ozone.api.model.Blob
import sh.christian.ozone.api.model.BlobRef

class CreatePostWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val postRepository: PostRepository,
    private val pendingUploadRepository: PendingUploadRepository,
) : CoroutineWorker(appContext, workerParameters), KoinComponent {
    private val uploadId = workerParameters.inputData.getLong("uploadId", 0L)

    override suspend fun doWork(): Result {
        if (uploadId == 0L) return Result.failure(workDataOf())

        val post =
            pendingUploadRepository.getPendingUploadsWithMediaById(uploadId)
                ?: return Result.failure(workDataOf())

        val facets = extractFacets(post.upload.text)

        val embed: PostEmbed =
            when {
                post.mediaAttachments.all { it.type == MediaType.IMAGE } -> {
                    val images =
                        post.mediaAttachments.map { media ->
                            ImageRef(
                                alt = media.altText ?: "",
                                image =
                                    Blob.StandardBlob(
                                        ref = BlobRef(Cid(media.remoteLink ?: "")),
                                        mimeType = media.mimeType ?: "",
                                        size = media.size ?: 0L,
                                    ),
                                aspectRatio = media.aspectRatio,
                            )
                        }
                    ImageEmbed(images = images)
                }

                post.mediaAttachments.all { it.type == MediaType.VIDEO } -> {
                    val video = post.mediaAttachments.first()
                    VideoEmbed(
                        video =
                            VideoRef(
                                video =
                                    Blob.StandardBlob(
                                        ref = BlobRef(Cid(video.remoteLink ?: "")),
                                        mimeType = video.mimeType ?: "",
                                        size = 0L,
                                    ),
                                aspectRatio = video.aspectRatio,
                            ),
                    )
                }

                else -> {
                    return Result.failure(
                        workDataOf(
                            "error" to "Unsupported media combination",
                            "uploadId" to uploadId,
                        ),
                    )
                }
            }

        val embedElement = OzoneJsonConfig.json.encodeToJsonElement(PostEmbed.serializer(), embed)

        val postRecord =
            PostRecord(
                text = post.upload.text,
                createdAt = Clock.System.now(),
                facets = facets,
                embed = embedElement,
                reply = post.upload.replyReference,
            )

        val createPostRequest =
            CreateRecord(
                repo = Did(post.upload.userDid),
                collection = Nsid("app.bsky.feed.post"),
                record = postRecord,
            )

        return when (postRepository.createPost(createPostRequest)) {
            is Response.Success -> {
                cleanup(post.upload)
            }

            is Response.Error -> {
                Result.retry()
            }
        }
    }

    private suspend fun cleanup(upload: PendingUploadEntity): Result {
        try {
            pendingUploadRepository.deleteUpload(upload)
            createNotification("Post uploaded successfully")
            return Result.success(
                workDataOf(
                    "resultType" to "success",
                    "uploadId" to uploadId,
                ),
            )
        } catch (e: Exception) {
            createNotification("Post upload failed")
            return Result.failure(
                workDataOf(
                    "resultType" to "failure",
                    "uploadId" to uploadId,
                    "error" to (e.message ?: "Upload failed"),
                ),
            )
        }
    }

    private fun createNotification(message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Post updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        notificationManager.createNotificationChannel(channel)
        val notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pulse Post")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun extractFacets(text: String): List<Facet> {
        val facets = mutableListOf<Facet>()
        val mentionRegex = "@[a-zA-Z0-9.-]+".toRegex()
        mentionRegex.findAll(text).forEach { matchResult ->
            facets.add(
                Facet(
                    index =
                        FacetByteSlice(
                            byteStart = matchResult.range.first.toLong(),
                            byteEnd = (matchResult.range.last + 1).toLong(),
                        ),
                    features =
                        listOf(
                            FacetFeatureUnion.Mention(
                                value = FacetMention(Did(matchResult.value.substring(1))),
                            ),
                        ),
                ),
            )
        }
        val urlRegex = "https?://\\S+".toRegex()
        urlRegex.findAll(text).forEach { matchResult ->
            facets.add(
                Facet(
                    index =
                        FacetByteSlice(
                            byteStart = matchResult.range.first.toLong(),
                            byteEnd = (matchResult.range.last + 1).toLong(),
                        ),
                    features =
                        listOf(
                            FacetFeatureUnion.Link(
                                value = FacetLink(Uri(matchResult.value)),
                            ),
                        ),
                ),
            )
        }

        return facets.sortedBy { it.index.byteStart }
    }

    companion object {
        const val CHANNEL_ID = "posts"
        const val NOTIFICATION_ID = 1
    }
}
