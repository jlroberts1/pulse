/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.data.network.request.CreateLikeRecordRequest
import com.contexts.pulse.data.network.request.UnlikeRecordRequest
import com.contexts.pulse.domain.model.LikeRecord
import com.contexts.pulse.domain.model.LikeSubject
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val feedRepository: FeedRepository,
    preferencesRepository: PreferencesRepository,
    private val postRepository: PostRepository,
    userRepository: UserRepository,
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

    val isLoggedIn =
        userRepository.isLoggedIn()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _feedStates =
        MutableStateFlow<Map<String, Flow<PagingData<TimelinePost>>>>(emptyMap())
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
                            current[feed.id] ?: feedRepository.getFeedPagingFlow(
                                feedId = feed.id,
                                feedUri = feed.uri,
                            )
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }
    }

    fun onLikeClicked(post: TimelinePost) {
        if (post.liked) unlikePost(post) else likePost(post)
    }

    private fun likePost(post: TimelinePost) {
        viewModelScope.launch {
            val currentUser = currentUser.firstOrNull()
            try {
                if (currentUser == null) throw Throwable("No current user")
                val likeRecord =
                    LikeRecord(
                        subject =
                            LikeSubject(
                                uri = post.uri.atUri,
                                cid = post.cid.cid,
                            ),
                    )
                val createLikeRecordRequest =
                    CreateLikeRecordRequest(
                        repo = currentUser,
                        collection = "app.bsky.feed.like",
                        record = likeRecord,
                    )
                postRepository.likePost(createLikeRecordRequest).onSuccess { response ->
                    feedRepository.likePost(post.uri.atUri, response.uri)
                }
            } catch (e: Exception) {
                logcat { "Error updating record, ${e.message}" }
            }
        }
    }

    private fun unlikePost(post: TimelinePost) {
        viewModelScope.launch {
            val currentUser = currentUser.firstOrNull()
            try {
                if (currentUser == null) throw Throwable("No current user")
                Uri.parse(post.likedUri?.atUri).lastPathSegment?.let {
                    val unlikeRecordRequest =
                        UnlikeRecordRequest(
                            repo = currentUser,
                            collection = "app.bsky.feed.like",
                            rkey = it,
                        )
                    postRepository.unlikePost(unlikeRecordRequest).onSuccess {
                        feedRepository.unlikePost(post.uri.atUri)
                    }
                }
            } catch (e: Exception) {
                logcat { "Error updating record ${e.message}" }
            }
        }
    }
}
