/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.addpost

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.ProfileViewBasic
import app.bsky.actor.SearchActorsTypeaheadQueryParams
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.ActorRepository
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
    val isLoading: Boolean = false,
    val charactersLeft: Int = 300,
    val image: ImageBitmap? = null,
    val error: String? = null,
)

class AddPostViewModel(private val actorRepository: ActorRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPostUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun handleTextChanged(
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

    private fun searchActors(query: String) {
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                delay(300)
                when (
                    val response =
                        actorRepository.searchActorsTypeahead(
                            SearchActorsTypeaheadQueryParams(
                                q = query,
                            ),
                        )
                ) {
                    is Response.Success -> {
                        _uiState.update {
                            it.copy(
                                suggestions = response.data.actors,
                                showSuggestions = response.data.actors.isNotEmpty(),
                                isLoading = false,
                                error = null,
                            )
                        }
                    }

                    is Response.Error -> {
                        _uiState.update {
                            it.copy(
                                error = "Failed to load suggestions",
                                isLoading = false,
                            )
                        }
                    }
                }
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

    fun onImageSelected(image: ImageBitmap?) {
        _uiState.update {
            it.copy(image = image)
        }
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
