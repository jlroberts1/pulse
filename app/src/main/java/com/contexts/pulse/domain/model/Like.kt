/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class LikeSubject(
    val uri: String,
    val cid: String,
)

@Serializable
data class LikeRecord(
    val subject: LikeSubject,
    val createdAt: String = Clock.System.now().toString(),
)

@Serializable
data class CreateLikeRecordRequest(
    val repo: String,
    val collection: String,
    val record: LikeRecord,
)

@Serializable
data class UnlikeRecordRequest(
    val repo: String,
    val collection: String,
    val rkey: String,
)

@Serializable
data class CreateLikeRecordResponse(
    val uri: String,
    val cid: String,
)
