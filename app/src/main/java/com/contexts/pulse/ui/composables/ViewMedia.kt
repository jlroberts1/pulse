/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.contexts.pulse.ui.components.Media3PlayerView

@Composable
fun ViewMedia(
    mediaUrl: String,
    onDismiss: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var imageFitsScreen by remember { mutableStateOf(true) }

    LaunchedEffect(imageFitsScreen, scale) {
        if (imageFitsScreen && scale <= 1f) {
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onDismiss() }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        if (!imageFitsScreen || scale > 1f) {
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                },
    ) {
        when {
            isVideoUrl(mediaUrl) -> Media3PlayerView(mediaUrl)
            else ->
                ZoomableImage(
                    imageUrl = mediaUrl,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    onImageFitsScreenChanged = { fits ->
                        imageFitsScreen = fits
                        if (fits && scale <= 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                )
        }

        IconButton(
            onClick = onDismiss,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(end = 16.dp, top = 16.dp)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = CircleShape,
                    ),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
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
    onImageFitsScreenChanged: (Boolean) -> Unit,
) {
    var imageSize by remember { mutableStateOf<IntSize?>(null) }
    var containerSize by remember { mutableStateOf<IntSize?>(null) }

    LaunchedEffect(imageSize, containerSize) {
        if (imageSize != null && containerSize != null) {
            val imageAspectRatio = imageSize!!.width.toFloat() / imageSize!!.height
            val containerAspectRatio = containerSize!!.width.toFloat() / containerSize!!.height

            val fits =
                if (imageAspectRatio > containerAspectRatio) {
                    imageSize!!.width <= containerSize!!.width
                } else {
                    imageSize!!.height <= containerSize!!.height
                }
            onImageFitsScreenChanged(fits)
        }
    }

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
        onSuccess = { state ->
            imageSize =
                IntSize(
                    state.painter.intrinsicSize.width.toInt(),
                    state.painter.intrinsicSize.height.toInt(),
                )
        },
        modifier =
            Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    containerSize = size
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                },
        contentScale = ContentScale.Fit,
    )
}

private fun isVideoUrl(url: String): Boolean {
    val videoExtensions = listOf(".mp4", ".mov", ".avi", ".wmv", ".flv", ".mkv", ".webm", ".m3u8")
    return videoExtensions.any { url.lowercase().endsWith(it) }
}
