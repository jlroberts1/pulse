/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.addpost

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.ProfileViewBasic
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.data.local.database.entities.PendingMediaAttachment
import com.contexts.pulse.data.local.database.entities.PendingUploadEntity
import com.contexts.pulse.data.local.database.entities.ReplyLink
import com.contexts.pulse.data.local.database.entities.ReplyReference
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.model.TenorGif
import com.contexts.pulse.domain.model.toPost
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.TenorRepository
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import com.contexts.pulse.worker.UploadWorkerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtUri
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class MediaItem(
    val uri: Uri,
    val mediaType: MediaType,
    val altText: String?,
)

class AddPostViewModel(
    private val actorRepository: ActorRepository,
    private val tenorRepository: TenorRepository,
    private val preferencesRepository: PreferencesRepository,
    private val pendingUploadRepository: PendingUploadRepository,
    private val uploadWorkerManager: UploadWorkerManager,
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPostUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    private val replyPost: String? =
        savedStateHandle[NavigationRoutes.Authenticated.AddPost.ARG_REPLY_POST]

    init {
        replyPost?.let { getReplyPost(it) }
    }

    private fun getReplyPost(replyPost: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val uri = AtUri(URLDecoder.decode(replyPost, StandardCharsets.UTF_8.name()))
            postRepository.getPosts(listOf(uri)).onSuccess { response ->
                _uiState.update { it.copy(replyPost = response.posts.first().toPost(), loading = false) }
            }.onError { error ->
                _uiState.update { it.copy(error = error.message, loading = false) }
            }
        }
    }

    suspend fun onGifSearchQueryChanged(newText: String) {
        delay(300)
        _uiState.update { it.copy(loading = true) }
        tenorRepository.searchTenor(newText).onSuccess { response ->
            _uiState.update { it.copy(gifSearchResults = response.results) }
        }.onError { error ->
            _uiState.update { it.copy(loading = false, error = error.message) }
        }
    }

    fun onUpload() {
        viewModelScope.launch {
            val currentUser = preferencesRepository.getCurrentUser()
            currentUser?.let {
                val replyPost = uiState.value.replyPost
                val parent = replyPost?.reply?.parent
                val rootReplyLink =
                    if (parent != null) {
                        ReplyLink(parent.uri.atUri, parent.cid.cid)
                    } else {
                        ReplyLink(replyPost?.uri?.atUri, replyPost?.cid?.cid)
                    }
                val pendingUpload =
                    PendingUploadEntity(
                        userDid = currentUser,
                        text = uiState.value.text,
                        replyReference =
                            replyPost?.let {
                                ReplyReference(
                                    parent =
                                        ReplyLink(
                                            uiState.value.replyPost?.uri?.atUri,
                                            uiState.value.replyPost?.cid?.cid,
                                        ),
                                    root = rootReplyLink,
                                )
                            },
                    )
                val uploadId = pendingUploadRepository.insertUpload(pendingUpload)
                val mediaItems = uiState.value.mediaItems
                if (mediaItems.isNotEmpty()) {
                    mediaItems.forEach { media ->
                        val mediaAttachment =
                            PendingMediaAttachment(
                                uploadId = uploadId,
                                type = media.mediaType,
                                localUri = media.uri.toString(),
                                altText = media.altText,
                            )
                        pendingUploadRepository.insertMediaAttachment(mediaAttachment)
                    }
                }
                uploadWorkerManager.upload(uploadId)
            }
            _uiState.update { it.copy(uploadSent = true) }
        }
    }

    fun handleMentionQueryChanged(
        newText: String,
        cursorPosition: Int,
    ) {
        _uiState.update {
            it.copy(
                text = newText,
                cursorPosition = cursorPosition,
                charactersLeft = 300 - newText.length,
            )
        }

        val mentionQuery = getCurrentMentionQuery(newText, cursorPosition)
        if (mentionQuery != null) {
            searchActors(mentionQuery)
        } else {
            dismissSuggestions()
        }
    }

    fun clearGifSearch() {
        _uiState.update {
            it.copy(
                gifSearchResults = emptyList(),
            )
        }
    }

    fun onGifSelected(gif: TenorGif) {
        _uiState.update {
            it.copy(
                selectedGif = gif,
            )
        }
    }

    fun onClearSelectedGif() {
        _uiState.update {
            it.copy(
                selectedGif = null,
            )
        }
    }

    fun handleActorSelected(actor: ProfileViewBasic) {
        val currentState = _uiState.value
        val text = currentState.text
        val cursorPosition = currentState.cursorPosition.coerceIn(0, text.length)

        val lastAtIndex =
            text.lastIndexOf("@", cursorPosition - 1)
                .takeIf { it >= 0 } ?: run {
                dismissSuggestions()
                return
            }

        try {
            val beforeMention = text.substring(0, lastAtIndex)
            val afterMention =
                if (cursorPosition <= text.length) {
                    text.substring(cursorPosition)
                } else {
                    ""
                }

            val mention = "@${actor.handle.handle} "
            val newText = beforeMention + mention + afterMention

            val newCursorPosition =
                (lastAtIndex + actor.handle.handle.length + 2)
                    .coerceIn(0, newText.length)

            _uiState.update {
                it.copy(
                    text = newText,
                    cursorPosition = newCursorPosition,
                    showSuggestions = false,
                    suggestions = emptyList(),
                )
            }
        } catch (e: Exception) {
            dismissSuggestions()
        }
    }

    fun onImagesSelected(mediaItems: List<MediaItem>) {
        _uiState.update {
            it.copy(mediaItems = mediaItems)
        }
    }

    private fun searchActors(query: String) {
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                _uiState.update { it.copy(loading = true) }

                delay(300)
                actorRepository.searchActorsTypeahead(
                    SearchActorsTypeaheadQueryParams(
                        q = query,
                    ),
                ).onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            suggestions = response.actors,
                            showSuggestions = response.actors.isNotEmpty(),
                            loading = false,
                            error = null,
                        )
                    }
                }.onError { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = "Failed to load suggestions",
                        )
                    }
                }
            }
    }

    private fun getCurrentMentionQuery(
        text: String,
        cursorPosition: Int,
    ): String? {
        val lastAtIndex = text.lastIndexOf("@", cursorPosition - 1)
        if (lastAtIndex == -1) return null

        val subsequentSpace = text.indexOf(" ", lastAtIndex)
        if (subsequentSpace != -1 && subsequentSpace < cursorPosition) return null

        val query = text.substring(lastAtIndex + 1, cursorPosition)
        return query.ifEmpty { null }
    }

    private fun dismissSuggestions() {
        _uiState.update {
            it.copy(
                showSuggestions = false,
                suggestions = emptyList(),
            )
        }
    }
}
