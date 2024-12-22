/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.addpost.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

enum class PickType {
    IMAGES,
    VIDEOS,
}

@Composable
fun rememberGalleryManager(
    pickType: PickType,
    onResult: (List<Uri>) -> Unit,
): GalleryManager {
    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uris ->
            onResult(uris)
        }
    val videoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onResult(listOf(uri)) }
        }
    return when (pickType) {
        PickType.IMAGES -> {
            remember {
                GalleryManager(onLaunch = {
                    imageLauncher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                })
            }
        }

        PickType.VIDEOS -> {
            remember {
                GalleryManager(onLaunch = {
                    videoLauncher.launch(
                        (
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly,
                            )
                        ),
                    )
                })
            }
        }
    }
}

class GalleryManager(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}
