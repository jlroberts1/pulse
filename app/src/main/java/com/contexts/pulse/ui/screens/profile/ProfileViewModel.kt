/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.pulse.data.local.database.entities.FeedPostEntity
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val feed: List<FeedPostEntity> = emptyList(),
    val feedLoading: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    val profile =
        profileRepository.getMyProfile()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    init {
        getFeed()
    }

    fun refreshProfile() {
        getFeed()
    }

    private fun getFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(feedLoading = true) }
            profileRepository.getProfileFeed().onSuccess { response ->
                val feed = response.feed.map { FeedPostEntity.from(it, "") }
                _uiState.update { it.copy(feed = feed, feedLoading = false) }
            }.onError { error ->
                _uiState.update { it.copy(feedLoading = false, error = error.message) }
            }
        }
    }
}
