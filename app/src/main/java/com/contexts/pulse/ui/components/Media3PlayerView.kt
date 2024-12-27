/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import logcat.logcat
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Media3PlayerView(videoUrl: String) {
    val viewModel: PlayerViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(videoUrl) {
        logcat("Media Player") { "Video received: $videoUrl" }
        viewModel.initializePlayer(videoUrl)
        onDispose { }
    }
    Box(modifier = Modifier) {
        AnimatedVisibility(
            visible = !uiState.loading && uiState.exoPlayer != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PlayerView(context).apply {
                        player = uiState.exoPlayer
                        useController = true
                    }
                },
                update = { view ->
                    view.player = uiState.exoPlayer
                },
            )
        }
    }

    if (uiState.loading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
