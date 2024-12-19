/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.converters

import androidx.room.TypeConverter
import app.bsky.embed.ExternalView
import app.bsky.embed.ImagesViewImage
import app.bsky.embed.RecordView
import app.bsky.embed.RecordWithMediaView
import app.bsky.embed.VideoView
import com.contexts.pulse.domain.model.Service
import com.contexts.pulse.domain.model.VerificationMethod
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}

class VerificationMethodConverter {
    @TypeConverter
    fun fromJson(value: String): List<VerificationMethod> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toJson(list: List<VerificationMethod>): String {
        return Json.encodeToString(list)
    }
}

class ServiceConverter {
    @TypeConverter
    fun fromJson(value: String): List<Service> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toJson(list: List<Service>): String {
        return Json.encodeToString(list)
    }
}

class InstantConvertor {
    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(epochMillis: Long): Instant {
        return Instant.fromEpochMilliseconds(epochMillis)
    }
}

class FeedConverters {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    @TypeConverter
    fun fromImagesList(images: List<ImagesViewImage>?): String? = images?.let { json.encodeToString(it) }

    @TypeConverter
    fun toImagesList(value: String?): List<ImagesViewImage>? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromExternalView(external: ExternalView?): String? = external?.let { json.encodeToString(it) }

    @TypeConverter
    fun toExternalView(value: String?): ExternalView? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromVideoView(video: VideoView?): String? = video?.let { json.encodeToString(it) }

    @TypeConverter
    fun toVideoView(value: String?): VideoView? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromRecordView(record: RecordView?): String? = record?.let { json.encodeToString(it) }

    @TypeConverter
    fun toRecordView(value: String?): RecordView? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromRecordWithMediaView(recordWithMedia: RecordWithMediaView?): String? = recordWithMedia?.let { json.encodeToString(it) }

    @TypeConverter
    fun toRecordWithMediaView(value: String?): RecordWithMediaView? = value?.let { json.decodeFromString(it) }
}
