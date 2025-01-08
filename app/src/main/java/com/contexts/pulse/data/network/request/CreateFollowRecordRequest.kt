/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.request

import com.contexts.pulse.domain.model.FollowRecord
import kotlinx.serialization.Serializable

@Serializable
data class CreateFollowRecordRequest(
    val repo: String,
    val collection: String,
    val record: FollowRecord,
)
