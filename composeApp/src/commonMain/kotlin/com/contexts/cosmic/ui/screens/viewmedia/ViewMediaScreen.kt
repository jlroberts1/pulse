/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ViewMediaScreen(mediaUrl: String) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
    ) {
        when {
            isVideoUrl(mediaUrl) -> VideoPlayer(mediaUrl)
            else ->
                ZoomableImage(
                    imageUrl = mediaUrl,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                )
        }
    }
}

@Composable
private fun ZoomableImage(
    imageUrl: String,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
) {
    SubcomposeAsyncImage(
        model =
            ImageRequest.Builder(LocalPlatformContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
        contentDescription = "Zoomable media",
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        },
        modifier =
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                },
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun VideoPlayer(videoUrl: String) {
}

private fun isVideoUrl(url: String): Boolean {
    val videoExtensions = listOf(".mp4", ".mov", ".avi", ".wmv", ".flv", ".mkv", ".webm")
    return videoExtensions.any { url.lowercase().endsWith(it) }
}
