/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TenorGif(
    val id: String,
    val title: String,
    @SerialName("media_formats")
    val mediaFormats: MediaFormats,
    @SerialName("content_description")
    val contentDescription: String,
    @SerialName("itemurl")
    val itemUrl: String,
    val url: String,
    val tags: List<String>,
    @SerialName("hasaudio")
    val hasAudio: Boolean,
)

@Serializable
data class MediaFormats(
    val gif: MediaFormat?,
    @SerialName("mediumgif")
    val mediumGif: MediaFormat?,
    @SerialName("tinygif")
    val tinyGif: MediaFormat?,
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
        mediumGif?.url
            ?: gif?.url
            ?: mp4?.url
            ?: webp?.url
            ?: tinyGif?.url
            ?: ""
    }
}
