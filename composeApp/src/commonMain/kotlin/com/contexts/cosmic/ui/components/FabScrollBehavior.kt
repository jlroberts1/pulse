/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@OptIn(ExperimentalMaterial3Api::class)
class FabScrollBehavior constructor(
    val topBarBehavior: TopAppBarScrollBehavior,
    private val onScroll: (Float) -> Unit,
) {
    val nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Delegate to original top bar behavior
                val topBarOffset =
                    topBarBehavior.nestedScrollConnection.onPreScroll(available, source)

                // Update FAB visibility through ViewModel
                onScroll(available.y)

                return topBarOffset
            }
        }
}
