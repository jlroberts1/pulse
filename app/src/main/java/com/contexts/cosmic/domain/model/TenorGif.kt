/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TenorGif(
    val id: String,
    val title: String,
    val media_formats: MediaFormats,
    val content_description: String,
    val itemurl: String,
    val url: String,
    val tags: List<String>,
    val hasaudio: Boolean,
)

@Serializable
data class MediaFormats(
    val gif: MediaFormat?,
    val mediumgif: MediaFormat?,
    val tinygif: MediaFormat?,
    val mp4: MediaFormat?,
    val webp: MediaFormat?,
)

@Serializable
data class MediaFormat(
    val url: String,
    val duration: Double,
    val dims: List<Int>,
    val size: Int,
)

fun TenorGif.getBestFormat(): String {
    return media_formats.run {
        mediumgif?.url
            ?: gif?.url
            ?: mp4?.url
            ?: webp?.url
            ?: tinygif?.url
            ?: ""
    }
}
