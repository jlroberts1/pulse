/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import app.bsky.embed.AspectRatio
import kotlinx.serialization.Serializable

@Serializable
data class EmbedVideo(
    val thumb: String?,
    val playlist: String,
    val aspectRatio: AspectRatio?,
)
