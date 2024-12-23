/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import app.bsky.video.GetJobStatusQueryParams
import app.bsky.video.GetJobStatusResponse
import app.bsky.video.UploadVideoResponse
import com.atproto.repo.UploadBlobResponse
import com.contexts.pulse.data.network.client.AccountManager
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.client.safeRequest
import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.path
import java.io.File

data class UploadParams(
    val file: File,
    val mimeType: String = "video/mp4",
)

class UploadAPI(
    client: HttpClient,
    accountManager: AccountManager,
) : BaseAPI(client, accountManager) {
    suspend fun uploadBlob(
        params: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadBlobResponse, NetworkError> {
        return client.safeRequest {
            configurePds()
            url {
                method = HttpMethod.Post
                path("/xrpc/com.atproto.repo.uploadBlob")
            }
            onUpload { bytesSentTotal, contentLength ->
                val progress =
                    when {
                        contentLength == null || contentLength == 0L -> 1f
                        else -> bytesSentTotal.toFloat() / contentLength.toFloat()
                    }
                onUploadProgress(progress)
            }
            setBody(
                ByteArrayContent(
                    params.file.readBytes(),
                    ContentType.parse(params.mimeType),
                ),
            )
        }
    }

    suspend fun uploadVideo(
        params: UploadParams,
        onUploadProgress: (Float) -> Unit,
    ): Response<UploadVideoResponse, NetworkError> {
        return client.safeRequest {
            configurePds()
            url {
                method = HttpMethod.Post
                path("/xrpc/app.bsky.video.uploadVideo")
            }
            onUpload { bytesSentTotal, contentLength ->
                val progress =
                    when {
                        contentLength == null || contentLength == 0L -> 1f
                        else -> bytesSentTotal.toFloat() / contentLength.toFloat()
                    }
                onUploadProgress(progress)
            }
            setBody(
                ByteArrayContent(
                    params.file.readBytes(),
                    ContentType.parse(params.mimeType),
                ),
            )
        }
    }

    suspend fun getVideoProcessingStatus(getJobStatusQueryParams: GetJobStatusQueryParams): Response<GetJobStatusResponse, NetworkError> {
        return client.safeRequest {
            configurePds()
            url {
                method = HttpMethod.Get
                path("/xrpc/app.bsky.video.getJobStatus")
                parameter("jobId", getJobStatusQueryParams.jobId)
            }
        }
    }
}
