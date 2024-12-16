/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TenorResponse(
    val results: List<TenorGif>,
)

@Serializable
data class TenorGif(
    val id: String,
    val title: String,
    @SerialName("media_formats")
    val mediaFormats: MediaFormats,
    @SerialName("content_description")
    val contentDescription: String,
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
    return mediaFormats.run {
        mediumgif?.url
            ?: gif?.url
            ?: mp4?.url
            ?: webp?.url
            ?: tinygif?.url
            ?: ""
    }
}
