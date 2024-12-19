/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.FeedViewPost
import com.contexts.cosmic.data.local.database.entities.ProfileEntity
import com.contexts.cosmic.data.network.client.onError
import com.contexts.cosmic.data.network.client.onSuccess
import com.contexts.cosmic.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: ProfileEntity? = null,
    val feed: List<FeedViewPost> = emptyList(),
    val feedLoading: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getProfile()
        getFeed()
    }

    fun refreshProfile() {
        getProfile()
        getFeed()
    }

    private fun getProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
        }
    }

    private fun getFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(feedLoading = true) }
            profileRepository.getProfileFeed().onSuccess { response ->
                _uiState.update { it.copy(feed = response.feed, feedLoading = false) }
            }.onError { error ->
                _uiState.update { it.copy(feedLoading = false, error = error.message) }
            }
        }
    }
}
