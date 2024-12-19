/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.contexts.cosmic.data.local.database.entities.FeedPostEntity
import com.contexts.cosmic.data.repository.FeedManager
import com.contexts.cosmic.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val feedManager: FeedManager,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val visibleFeeds =
        preferencesRepository.getCurrentUserFlow().flatMapLatest { user ->
            feedManager.getAvailableFeeds(user).map { feeds -> feeds.filter { it.pinned } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val allFeeds =
        preferencesRepository.getCurrentUserFlow().flatMapLatest { user ->
            feedManager.getAvailableFeeds(user)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _feedStates =
        MutableStateFlow<Map<String, Flow<PagingData<FeedPostEntity>>>>(emptyMap())
    val feedStates = _feedStates.asStateFlow()

    init {
        viewModelScope.launch {
            visibleFeeds.collect { feeds ->
                _feedStates.update { current ->
                    feeds.associate { feed ->
                        feed.id to (
                            current[feed.id] ?: feedManager.getFeedPagingFlow(
                                feed.id,
                                feed.uri,
                            ).cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }
    }
}
