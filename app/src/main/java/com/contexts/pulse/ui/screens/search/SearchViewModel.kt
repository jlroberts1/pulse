/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.GetSuggestionsQueryParams
import app.bsky.actor.ProfileView
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetSuggestedFeedsQueryParams
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.FeedRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val suggestedUsers: List<ProfileView> = emptyList(),
    val suggestedFeeds: List<GeneratorView> = emptyList(),
    val error: String? = null,
)

class SearchViewModel(
    private val actorRepository: ActorRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getSuggestions()
    }

    private fun getSuggestions() {
        viewModelScope.launch {
            val users = async { actorRepository.getSuggestions(GetSuggestionsQueryParams()) }
            val feeds = async { feedRepository.getSuggestions(GetSuggestedFeedsQueryParams()) }
            val usersResult = users.await()
            val feedsResult = feeds.await()
            usersResult.onSuccess { response ->
                _uiState.update { it.copy(suggestedUsers = response.actors) }
            }.onError { error ->
                _uiState.update { it.copy(error = error.message) }
            }
            feedsResult.onSuccess { response ->
                Log.d("SearchViewModel", "${response.feeds}")
                _uiState.update { it.copy(suggestedFeeds = response.feeds) }
            }.onError { error ->
                Log.d("SearchViewModel", error.message)
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }
}
