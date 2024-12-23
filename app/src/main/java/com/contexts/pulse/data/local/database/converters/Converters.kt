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
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.data.local.database.entities.MediaUploadState
import com.contexts.pulse.data.local.database.entities.PostUploadState
import com.contexts.pulse.data.local.database.entities.VideoProcessingState
import com.contexts.pulse.domain.model.Service
import com.contexts.pulse.domain.model.VerificationMethod
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromVerificationJson(value: String): List<VerificationMethod> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toVerificationJson(list: List<VerificationMethod>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun fromServiceJson(value: String): List<Service> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toServiceJson(list: List<Service>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(epochMillis: Long): Instant {
        return Instant.fromEpochMilliseconds(epochMillis)
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromMediaType(type: MediaType): String {
        return type.name
    }

    @TypeConverter
    fun toMediaUploadState(value: String): MediaUploadState {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromMediaUploadState(state: MediaUploadState): String {
        return state.name
    }

    @TypeConverter
    fun toPostUploadState(value: String): PostUploadState {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromPostUploadState(state: PostUploadState): String {
        return state.name
    }

    @TypeConverter
    fun toVideoProcessingState(value: String): VideoProcessingState {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromVideoProcessingState(state: VideoProcessingState): String {
        return state.name
    }
}
