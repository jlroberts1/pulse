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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.ProfileViewBasic
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.model.TenorGif
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.TenorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPostUiState(
    val text: String = "",
    val cursorPosition: Int = 0,
    val suggestions: List<ProfileViewBasic> = emptyList(),
    val showSuggestions: Boolean = false,
    val loading: Boolean = false,
    val charactersLeft: Int = 300,
    val imageUris: List<Uri> = emptyList(),
    val gifSearchResults: List<TenorGif> = emptyList(),
    val selectedGif: TenorGif? = null,
    val error: String? = null,
)

class AddPostViewModel(
    private val actorRepository: ActorRepository,
    private val tenorRepository: TenorRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPostUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    suspend fun onGifSearchQueryChanged(newText: String) {
        delay(300)
        _uiState.update { it.copy(loading = true) }
        tenorRepository.searchTenor(newText).onSuccess { response ->
            _uiState.update { it.copy(gifSearchResults = response.results) }
        }.onError { error ->
            _uiState.update { it.copy(loading = false, error = error.message) }
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

    fun onImagesSelected(imageUris: List<Uri>) {
        _uiState.update {
            it.copy(imageUris = imageUris)
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
