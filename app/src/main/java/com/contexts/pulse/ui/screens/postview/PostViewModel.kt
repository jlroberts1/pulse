/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetPostThreadResponseThreadUnion
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostViewUiState(
    val threadViewPost: GetPostThreadResponseThreadUnion.ThreadViewPost? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class PostViewModel(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel() {
    private val postId: String = checkNotNull(savedStateHandle[NavigationRoutes.Authenticated.PostView.ARG_POST_ID])

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
                                threadViewPost = response.thread as GetPostThreadResponseThreadUnion.ThreadViewPost,
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
}
