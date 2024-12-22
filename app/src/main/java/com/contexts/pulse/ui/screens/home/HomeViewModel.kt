/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.bsky.feed.FeedViewPost
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val feedRepository: FeedRepository,
    preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val currentUser = preferencesRepository.getCurrentUserFlow()
    private val allFeedsFlow =
        currentUser
            .mapNotNull { it }
            .flatMapLatest { user -> feedRepository.getAvailableFeeds(user) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val visibleFeeds =
        allFeedsFlow.map { feeds -> feeds.filter { it.pinned } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _feedStates =
        MutableStateFlow<Map<String, Flow<PagingData<FeedViewPost>>>>(emptyMap())
    val feedStates = _feedStates.asStateFlow()

    init {
        getFeeds()
    }

    private fun getFeeds() {
        viewModelScope.launch {
            visibleFeeds.collect { feeds ->
                _feedStates.update { current ->
                    feeds.associate { feed ->
                        feed.id to (
                            current[feed.id] ?: feedRepository.getFeed(feed.uri)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }
    }
}
