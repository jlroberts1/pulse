/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetPostThreadResponseThreadUnion
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.data.network.request.CreateLikeRecordRequest
import com.contexts.pulse.data.network.request.UnlikeRecordRequest
import com.contexts.pulse.domain.model.LikeRecord
import com.contexts.pulse.domain.model.LikeSubject
import com.contexts.pulse.domain.model.Thread
import com.contexts.pulse.domain.model.ThreadPost
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.toThread
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat

data class PostViewUiState(
    val thread: Thread? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class PostViewModel(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val postId: String = checkNotNull(savedStateHandle[NavigationRoutes.Authenticated.PostView.ARG_POST_ID])
    private val currentUser = preferencesRepository.getCurrentUserFlow()
    private val _uiState = MutableStateFlow(PostViewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getThread()
    }

    fun getThread() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            postRepository.getPostThread(postId).onSuccess { response ->
                when (response.thread) {
                    is GetPostThreadResponseThreadUnion.ThreadViewPost -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                thread = response.thread.value.toThread(),
                            )
                        }
                    }
                    is GetPostThreadResponseThreadUnion.BlockedPost,
                    is GetPostThreadResponseThreadUnion.NotFoundPost,
                    -> Unit
                }
            }.onError { error ->
                _uiState.update { it.copy(loading = false, error = error.message) }
            }
        }
    }

    fun onLikeClicked(post: TimelinePost) {
        _uiState.update {
            it.copy(
                thread =
                    it.thread?.updatePostByUri(post.uri.atUri) { post ->
                        post.copy(
                            liked = !post.liked,
                            likeCount = if (post.liked) post.likeCount - 1 else post.likeCount + 1,
                        )
                    },
            )
        }
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

    private fun Thread.updatePostByUri(
        uri: String,
        update: (TimelinePost) -> TimelinePost,
    ): Thread {
        fun ThreadPost.updateReplies(): ThreadPost {
            return when (this) {
                is ThreadPost.ViewablePost -> {
                    if (post.uri.atUri == uri) {
                        this.copy(post = update(post), replies = replies.map { it.updateReplies() })
                    } else {
                        this.copy(replies = replies.map { it.updateReplies() })
                    }
                }
                ThreadPost.NotFoundPost, ThreadPost.BlockedPost -> this
            }
        }

        return this.copy(
            post = if (post.uri.atUri == uri) update(post) else post,
            parents = parents.map { it.updateReplies() },
            replies = replies.map { it.updateReplies() },
        )
    }
}
