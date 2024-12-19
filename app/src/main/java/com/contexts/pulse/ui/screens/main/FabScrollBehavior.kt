/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@OptIn(ExperimentalMaterial3Api::class)
class FabScrollBehavior(
    val topBarBehavior: TopAppBarScrollBehavior,
    private val onScroll: (Float) -> Unit,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    val nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val topBarOffset =
                    topBarBehavior.nestedScrollConnection.onPreScroll(available, source)
                onScroll(available.y)

                return topBarOffset
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFabScrollBehavior(onScroll: (Float) -> Unit): FabScrollBehavior {
    val topBarState = rememberTopAppBarState()
    val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)

    return remember(topBarBehavior) {
        FabScrollBehavior(topBarBehavior, onScroll)
    }
}
