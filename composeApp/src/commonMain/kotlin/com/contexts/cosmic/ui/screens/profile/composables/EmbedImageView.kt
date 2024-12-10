package com.contexts.cosmic.ui.screens.profile.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.ImageView

@Composable
fun EmbedImageView(images: List<ImageView>) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier =
            Modifier.height(200.dp)
                .fillMaxWidth(),
    ) { page ->
        AsyncImage(
            model = images[page].fullsize,
            contentDescription = images[page].alt,
            modifier =
                Modifier
                    .padding(end = 4.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}
