/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import app.bsky.actor.PreferencesUnion
import app.bsky.actor.PutPreferencesRequest
import app.bsky.actor.SavedFeed
import app.bsky.actor.SavedFeedsPrefV2
import app.bsky.actor.Type
import app.bsky.feed.GeneratorView
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.data.network.request.CreateFollowRecordRequest
import com.contexts.pulse.domain.model.FollowRecord
import com.contexts.pulse.domain.repository.ActorRepository
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import sh.christian.ozone.api.Did

class SearchViewModel(
    actorRepository: ActorRepository,
    feedRepository: FeedRepository,
    private val preferencesRepository: PreferencesRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _refreshAccounts = MutableStateFlow<String?>(null)
    val refreshAccounts = _refreshAccounts.asStateFlow()

    private val _refreshFeeds = MutableStateFlow<String?>(null)
    val refreshFeeds = _refreshFeeds.asStateFlow()

    val suggestedAccounts =
        actorRepository.getSuggestions()
            .cachedIn(viewModelScope)

    val suggestedFeeds =
        feedRepository.getSuggestions()
            .cachedIn(viewModelScope)

    fun addFeed(feed: GeneratorView) {
        viewModelScope.launch {
            val savedFeed =
                SavedFeed(
                    id = feed.did.did,
                    type = Type.Feed,
                    pinned = true,
                    value = feed.uri.atUri,
                )
            val preferences =
                PreferencesUnion.SavedFeedsPrefV2(
                    value = SavedFeedsPrefV2(listOf(savedFeed)),
                )
            val putPreferencesRequest = PutPreferencesRequest(listOf(preferences))
            profileRepository.putPreferences(putPreferencesRequest).onSuccess {
                _refreshFeeds.update { feed.displayName }
            }
        }
    }

    fun followAccount(
        did: Did,
        handle: String,
    ) {
        viewModelScope.launch {
            val user = preferencesRepository.getCurrentUser()
            user?.let {
                val followRecord =
                    FollowRecord(
                        subject = did.did,
                        createdAt = Clock.System.now().toString(),
                    )
                val request =
                    CreateFollowRecordRequest(
                        repo = it,
                        collection = "app.bsky.graph.follow",
                        record = followRecord,
                    )
                profileRepository.followUser(request).onSuccess {
                    _refreshAccounts.update { handle }
                }
            }
        }
    }

    fun clearRefreshedAccount() {
        _refreshAccounts.update { null }
    }

    fun clearRefreshedFeed() {
        _refreshFeeds.update { null }
    }
}
