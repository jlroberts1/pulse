/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.contexts.cosmic.ui.composables.BorderedCircularAvatar
import com.contexts.cosmic.ui.composables.FeedItemInteractions
import com.contexts.cosmic.ui.composables.Loading
import com.contexts.cosmic.ui.composables.PostHeader
import com.contexts.cosmic.ui.composables.PostMessageText
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

// Catch all for now Previews, since commonMain previews don't work..
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PostHeaderPreview() {
    PostHeader(
        displayName = "Test name",
        handle = "test.handle",
        indexedAt = Instant.now().toKotlinInstant(),
    )
}

@Preview
@Composable
fun PostMessageTextPreview() {
    PostMessageText()
}

@Preview
@Composable
fun BorderedCircularAvatarPreview() {
    BorderedCircularAvatar()
}

@Preview
@Composable
fun FeedItemInteractionBarPreview() {
    FeedItemInteractions()
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}
