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
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    savedStateHandle: SavedStateHandle,
    postRepository: PostRepository,
) : ViewModel() {
    private val postId: String = checkNotNull(savedStateHandle[NavigationRoutes.Authenticated.PostView.ARG_POST_ID])
    private val _thread = MutableStateFlow<GetPostThreadResponseThreadUnion.ThreadViewPost?>(null)
    val thread = _thread.asStateFlow()

    init {
        viewModelScope.launch {
            postRepository.getPostThread(postId).onSuccess { response ->
                when (response.thread) {
                    is GetPostThreadResponseThreadUnion.ThreadViewPost -> {
                        _thread.update { response.thread as GetPostThreadResponseThreadUnion.ThreadViewPost }
                    }
                    is GetPostThreadResponseThreadUnion.BlockedPost,
                    is GetPostThreadResponseThreadUnion.NotFoundPost,
                    is GetPostThreadResponseThreadUnion.Unknown,
                    -> Unit
                }
            }
        }
    }
}
