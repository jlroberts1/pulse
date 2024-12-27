/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.extensions

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.webkit.MimeTypeMap
import app.bsky.embed.AspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.logcat
import java.io.File
import java.io.IOException

fun File.getAspectRatio(context: Context): AspectRatio? {
    return when {
        isImageFile() -> getImageAspectRatio()
        isVideoFile() -> getVideoAspectRatio(context)
        else -> null
    }
}

private fun File.isImageFile(): Boolean {
    return path.lowercase().run {
        endsWith(".jpg") || endsWith(".jpeg") ||
            endsWith(".png") || endsWith(".webp")
    }
}

private fun File.isVideoFile(): Boolean {
    return path.lowercase().run {
        endsWith(".mp4") || endsWith(".mov") ||
            endsWith(".webm") || endsWith(".mkv")
    }
}

private fun File.getImageAspectRatio(): AspectRatio? {
    return try {
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
        BitmapFactory.decodeFile(absolutePath, options)

        if (options.outWidth != -1 && options.outHeight != -1) {
            AspectRatio(options.outWidth.toLong(), options.outHeight.toLong())
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

private fun File.getVideoAspectRatio(context: Context): AspectRatio? {
    return try {
        MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, Uri.fromFile(this))

            val width =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    ?.toIntOrNull()
            val height =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ?.toIntOrNull()

            if (width != null && height != null && height != 0) {
                AspectRatio(width = width.toLong(), height = height.toLong())
            } else {
                null
            }
        }
    } catch (e: Exception) {
        null
    }
}

fun File.getMimeType(context: Context): String? {
    val filename = name

    logcat(
        "MimeType",
    ) {
        """
        File debug info:
        - Name: $filename
        - Exists: ${exists()}
        - Can read: ${canRead()}
        - Length: ${length()}
        - Path: $absolutePath
        - Extension: $extension
        - First 16 bytes: ${readFirstBytes()}
        """.trimIndent()
    }

    if (extension.isBlank()) {
        detectMimeTypeFromContents()?.also { mimeType ->
            logcat("MimeType") { "Found mime type from magic numbers: $mimeType" }
            return mimeType
        }
    }

    val extension = extension.lowercase()
    logcat("MimeType") { "Attempting to get mime type for file: $filename with extension: $extension" }

    return try {
        try {
            val uri = Uri.fromFile(this)
            context.contentResolver.getType(uri)?.also { mimeType ->
                logcat("MimeType") { "Found mime type from ContentResolver: $mimeType" }
                return mimeType
            }
        } catch (e: Exception) {
            logcat("MimeType") { "ContentResolver failed to get mime type, $e" }
        }

        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.also { mimeType ->
            logcat("MimeType") { "Found mime type from MimeTypeMap: $mimeType" }
            return mimeType
        }

        commonTypesMapping(extension)?.also { mimeType ->
            logcat("MimeType") { "Found mime type from common types mapping: $mimeType" }
            return mimeType
        }

        logcat("MimeType") { "All attempts to get mime type failed for file: $filename" }
        null
    } catch (e: Exception) {
        logcat("MimeType") { "Exception while getting mime type for file: $filename, $e" }
        null
    }
}

private fun File.readFirstBytes(): String {
    return try {
        inputStream().use { input ->
            val bytes = ByteArray(16)
            val read = input.read(bytes)
            if (read > 0) {
                bytes.take(read).joinToString(" ") { String.format("%02X", it) }
            } else {
                "Could not read bytes"
            }
        }
    } catch (e: Exception) {
        "Error reading bytes: ${e.message}"
    }
}

private fun File.detectMimeTypeFromContents(): String? {
    return try {
        inputStream().use { input ->
            val bytes = ByteArray(16)
            val read = input.read(bytes)
            if (read > 0) {
                when {
                    bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() &&
                        bytes[2] == 0xFF.toByte() -> "image/jpeg"

                    bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() &&
                        bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() -> "image/png"

                    bytes[0] == 0x47.toByte() && bytes[1] == 0x49.toByte() &&
                        bytes[2] == 0x46.toByte() && bytes[3] == 0x38.toByte() -> "image/gif"

                    bytes[4] == 0x66.toByte() && bytes[5] == 0x74.toByte() &&
                        bytes[6] == 0x79.toByte() && bytes[7] == 0x70.toByte() -> "video/mp4"

                    else -> null
                }
            } else {
                null
            }
        }
    } catch (e: Exception) {
        logcat("MimeType") { "Error detecting mime type from contents, $e" }
        null
    }
}

private fun commonTypesMapping(extension: String): String? {
    return when (extension) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "mp4" -> "video/mp4"
        "mov" -> "video/quicktime"
        "webm" -> "video/webm"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        "bmp" -> "image/bmp"
        "svg" -> "image/svg+xml"
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "m4a" -> "audio/mp4"
        "txt" -> "text/plain"
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        else -> null
    }
}

suspend fun copyUriToTempFile(
    context: Context,
    uri: Uri,
): Result<File> {
    return try {
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val extension =
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType) ?: when {
                mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
                mimeType.contains("png") -> "png"
                mimeType.contains("gif") -> "gif"
                mimeType.contains("mp4") -> "mp4"
                mimeType.contains("webm") -> "webm"
                mimeType.contains("webp") -> "webp"
                else -> ""
            }

        val tempFile =
            File(
                context.cacheDir,
                "upload_${System.currentTimeMillis()}${if (extension.isNotEmpty()) ".$extension" else ""}",
            )

        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Failed to open input stream")
        }

        if (!tempFile.exists() || tempFile.length() == 0L) {
            tempFile.delete()
            throw IOException("Failed to copy file or file is empty")
        }

        logcat(
            "FileUpload",
        ) {
            """
            Temporary file created:
            - Path: ${tempFile.absolutePath}
            - Size: ${tempFile.length()}
            - Mime type: $mimeType
            - Extension: $extension
            """.trimIndent()
        }
        Result.success(tempFile)
    } catch (e: Exception) {
        logcat("FileUpload") { "Failed to copy uri to temp file, $e" }
        Result.failure(e)
    }
}
