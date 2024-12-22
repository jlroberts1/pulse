/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contexts.pulse.extensions.getPostText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostViewScreen(
    viewModel: PostViewModel = koinViewModel(),
    postId: String,
) {
    val postThread by viewModel.thread.collectAsStateWithLifecycle()

    postThread?.value?.post?.let {
        Text(
            postId,
        )
        Text(
            it.getPostText(),
        )
    }
}
