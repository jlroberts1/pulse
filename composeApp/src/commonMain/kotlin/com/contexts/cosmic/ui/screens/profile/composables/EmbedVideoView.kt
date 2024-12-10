package com.contexts.cosmic.ui.screens.profile.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.AspectRatio

@Composable
fun EmbedVideoView(
    thumbnail: String,
    playlist: String,
    aspectRatio: AspectRatio,
) {
    AsyncImage(
        model = thumbnail,
        contentDescription = null,
        modifier =
            Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.FillWidth,
    )
}
