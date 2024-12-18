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
import app.bsky.feed.FeedViewPost
import com.contexts.cosmic.data.network.client.onError
import com.contexts.cosmic.data.network.client.onSuccess
import com.contexts.cosmic.domain.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedState(
    val feed: List<FeedViewPost> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

sealed class FeedType(val title: String) {
    data object Discover : FeedType("Discover")

    data object Following : FeedType("Following")

    companion object {
        val entries = listOf(Discover, Following)
    }
}

class HomeViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _visibleFeeds = MutableStateFlow(listOf(FeedType.Discover, FeedType.Following))
    val visibleFeeds = _visibleFeeds.asStateFlow()

    private val _availableFeeds = MutableStateFlow(FeedType.entries)
    val availableFeeds = _availableFeeds.asStateFlow()
    private val _followingFeed = MutableStateFlow(FeedState())
    val followingFeed = _followingFeed.asStateFlow()

    private val _discoverFeed = MutableStateFlow(FeedState())
    val discoverFeed = _discoverFeed.asStateFlow()

    init {
        loadFeeds()
    }

    fun loadFeeds() {
        loadDiscover()
        loadFollowing()
    }

    private fun loadDiscover() {
        viewModelScope.launch {
            _discoverFeed.update { it.copy(loading = true, error = null) }
            feedRepository.getDefaultFeed().onSuccess { response ->
                _discoverFeed.update { it.copy(feed = response.feed, loading = false) }
            }.onError { error ->
                _discoverFeed.update { it.copy(loading = false, error = error.message) }
            }
        }
    }

    private fun loadFollowing() {
        viewModelScope.launch {
            _followingFeed.update { it.copy(loading = true, error = null) }
            feedRepository.getTimeline().onSuccess { response ->
                _followingFeed.update { it.copy(feed = response.feed, loading = false) }
            }.onError { error ->
                _followingFeed.update { it.copy(loading = false, error = error.message) }
            }
        }
    }
}
