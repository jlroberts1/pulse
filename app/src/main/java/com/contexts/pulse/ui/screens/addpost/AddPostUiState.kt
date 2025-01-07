/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.addpost

import app.bsky.actor.ProfileViewBasic
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.domain.model.TenorGif
import com.contexts.pulse.domain.model.TimelinePost

data class AddPostUiState(
    val text: String = "",
    val cursorPosition: Int = 0,
    val suggestions: List<ProfileViewBasic> = emptyList(),
    val showSuggestions: Boolean = false,
    val loading: Boolean = false,
    val charactersLeft: Int = 300,
    val mediaItems: List<MediaItem> = emptyList(),
    val gifSearchResults: List<TenorGif> = emptyList(),
    val selectedGif: TenorGif? = null,
    val error: String? = null,
    val uploadSent: Boolean = false,
    val replyPost: TimelinePost? = null,
) {
    private val hasVideos: Boolean
        get() = mediaItems.any { it.mediaType == MediaType.VIDEO }

    private val hasImages: Boolean
        get() = mediaItems.any { it.mediaType == MediaType.IMAGE }

    private val hasGif: Boolean
        get() = selectedGif != null

    val canAddImages: Boolean
        get() = !hasVideos && !hasGif && mediaItems.size < MAX_IMAGES

    val canAddVideos: Boolean
        get() = !hasImages && !hasGif && mediaItems.isEmpty()

    val canAddGif: Boolean
        get() = mediaItems.isEmpty() && !hasGif

    val hasCharactersLeft: Boolean
        get() = charactersLeft >= 0

    val hasContent: Boolean
        get() = text.isNotEmpty() || hasGif || hasImages || hasVideos

    val mediaTypeUnavailableReason: String?
        get() =
            when {
                hasVideos -> "Cannot add other media while a video is present"
                hasImages -> "Cannot add videos or GIFs while images are present"
                hasGif -> "Cannot add other media while a GIF is present"
                mediaItems.size >= MAX_IMAGES -> "Maximum number of images reached"
                else -> null
            }

    val canStartUpload: Boolean
        get() = hasCharactersLeft && hasContent

    val cantUploadReason: String?
        get() =
            when {
                !hasCharactersLeft -> "Message to long to send, please shorten message to send"
                !hasContent -> "No content"
                else -> null
            }

    companion object {
        const val MAX_IMAGES = 4
    }
}
