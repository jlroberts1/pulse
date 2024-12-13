/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryManager(onResult: (SharedImage?) -> Unit): GalleryManager

expect class GalleryManager(
    onLaunch: () -> Unit,
) {
    fun launch()
}
