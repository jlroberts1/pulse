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
                val topBarOffset = topBarBehavior.nestedScrollConnection.onPreScroll(available, source)

                // Update FAB visibility through ViewModel
                onScroll(available.y)

                return topBarOffset
            }
        }
}
