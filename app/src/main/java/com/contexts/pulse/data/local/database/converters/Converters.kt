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
import app.bsky.embed.AspectRatio
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.data.local.database.entities.MediaUploadState
import com.contexts.pulse.data.local.database.entities.PostUploadState
import com.contexts.pulse.data.local.database.entities.ReplyReference
import com.contexts.pulse.data.local.database.entities.VideoProcessingState
import com.contexts.pulse.data.network.client.OzoneJsonConfig
import com.contexts.pulse.domain.model.Label
import com.contexts.pulse.domain.model.Profile
import com.contexts.pulse.domain.model.Service
import com.contexts.pulse.domain.model.TimelinePostFeature
import com.contexts.pulse.domain.model.TimelinePostLink
import com.contexts.pulse.domain.model.TimelinePostReason
import com.contexts.pulse.domain.model.TimelinePostReply
import com.contexts.pulse.domain.model.VerificationMethod
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.ListSerializer
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
    fun fromAspectRatioJson(value: String): AspectRatio {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toAspectRatioJson(aspectRatio: AspectRatio): String {
        return Json.encodeToString(aspectRatio)
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

    @TypeConverter
    fun toReplyReference(value: String): ReplyReference {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromReplyReference(reference: ReplyReference): String {
        return Json.encodeToString(reference)
    }

    @TypeConverter
    fun fromProfile(profile: Profile): String {
        return Json.encodeToString(Profile.serializer(), profile)
    }

    @TypeConverter
    fun toProfile(json: String): Profile {
        return Json.decodeFromString(Profile.serializer(), json)
    }

    @TypeConverter
    fun fromTimelinePostLinks(links: List<TimelinePostLink>): String {
        return Json.encodeToString(ListSerializer(TimelinePostLink.serializer()), links)
    }

    @TypeConverter
    fun toTimelinePostLinks(json: String): List<TimelinePostLink> {
        return Json.decodeFromString(ListSerializer(TimelinePostLink.serializer()), json)
    }

    @TypeConverter
    fun fromLabels(labels: List<Label>): String {
        return Json.encodeToString(ListSerializer(Label.serializer()), labels)
    }

    @TypeConverter
    fun toLabels(json: String): List<Label> {
        return Json.decodeFromString(ListSerializer(Label.serializer()), json)
    }

    @TypeConverter
    fun fromTimelinePostReply(reply: TimelinePostReply): String {
        return Json.encodeToString(TimelinePostReply.serializer(), reply)
    }

    @TypeConverter
    fun toTimelinePostReply(value: String): TimelinePostReply {
        return Json.decodeFromString(TimelinePostReply.serializer(), value)
    }

    @TypeConverter
    fun fromTimelinePostReason(reason: TimelinePostReason): String {
        return Json.encodeToString(TimelinePostReason.serializer(), reason)
    }

    @TypeConverter
    fun toTimelinePostReason(value: String): TimelinePostReason {
        return Json.decodeFromString(TimelinePostReason.serializer(), value)
    }

    @TypeConverter
    fun fromTimelinePostFeature(feature: TimelinePostFeature): String {
        return Json.encodeToString(TimelinePostFeature.serializer(), feature)
    }

    @TypeConverter
    fun toTimelinePostFeature(value: String): TimelinePostFeature {
        return Json.decodeFromString(TimelinePostFeature.serializer(), value)
    }

    companion object {
        private val Json = OzoneJsonConfig.json
    }
}
