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
import app.bsky.richtext.FacetLink
import app.bsky.richtext.FacetMention
import com.atproto.repo.CreateRecordRequest
import com.contexts.pulse.R
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.model.PostRecord
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.model.JsonContent

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
            pendingUploadRepository.getPendingUploadById(uploadId)
                ?: return Result.failure(workDataOf())

        val facets = extractFacets(post.text)
        val postRecord =
            PostRecord(
                text = post.text,
                createdAt = Clock.System.now(),
                facets = facets,
            )

        val jsonElement = Json.encodeToJsonElement(postRecord)
        val jsonContent = JsonContent(value = jsonElement, format = Json)

        val createPostRequest =
            CreateRecordRequest(
                repo = Did(post.userDid),
                collection = Nsid("app.bsky.feed.post"),
                record = jsonContent,
            )

        return when (postRepository.createPost(createPostRequest)) {
            is Response.Success -> {
                cleanup(post)
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
                            app.bsky.richtext.FacetFeatureUnion.Mention(
                                value = FacetMention(Did(matchResult.value.substring(1))),
                            ),
                        ),
                ),
            )
        }
        val urlRegex = "https?://[^\\s]+".toRegex()
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
                            app.bsky.richtext.FacetFeatureUnion.Link(
                                value = FacetLink(sh.christian.ozone.api.Uri(matchResult.value)),
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
