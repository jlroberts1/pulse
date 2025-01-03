/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.addpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.contexts.pulse.data.local.database.entities.MediaType
import com.contexts.pulse.domain.model.getBestFormat
import com.contexts.pulse.ui.composables.ScaffoldedScreen
import com.contexts.pulse.ui.screens.addpost.composables.AddPostBottomBar
import com.contexts.pulse.ui.screens.addpost.composables.AddPostSuggestions
import com.contexts.pulse.ui.screens.addpost.composables.PickType
import com.contexts.pulse.ui.screens.addpost.composables.TenorSearch
import com.contexts.pulse.ui.screens.addpost.composables.rememberGalleryManager
import com.contexts.pulse.ui.screens.postview.composables.PostViewItem
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPostScreen(
    replyPost: String?,
    viewModel: AddPostViewModel = koinViewModel(),
    navController: NavController,
    drawerState: DrawerState,
    onPostSent: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var launchImageGallery by remember { mutableStateOf(value = false) }
    var launchVideoGallery by remember { mutableStateOf(value = false) }
    var launchGif by remember { mutableStateOf(value = false) }

    DisposableEffect(Unit) {
        onDispose {
            logcat("Navigation") { "Add post disposing" }
        }
    }

    val imagePicker =
        rememberGalleryManager(pickType = PickType.IMAGES) {
            scope.launch {
                launchImageGallery = false
                val mediaItems =
                    it.map {
                        MediaItem(
                            uri = it,
                            mediaType = MediaType.IMAGE,
                            altText = "",
                        )
                    }
                viewModel.onImagesSelected(mediaItems)
            }
        }

    val videoPicker =
        rememberGalleryManager(pickType = PickType.VIDEOS) {
            scope.launch {
                launchVideoGallery = false
                val mediaItems =
                    it.map {
                        MediaItem(
                            uri = it,
                            mediaType = MediaType.VIDEO,
                            altText = "",
                        )
                    }
                viewModel.onImagesSelected(mediaItems)
            }
        }

    if (uiState.uploadSent) {
        onPostSent()
    }

    if (launchImageGallery) {
        imagePicker.launch()
    }

    if (launchVideoGallery) {
        videoPicker.launch()
    }

    ScaffoldedScreen(
        navController = navController,
        title = "Add Post",
        drawerState = drawerState,
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                uiState.replyPost?.let { post ->
                    PostViewItem(
                        post = post,
                        onPostClick = {},
                        onReplyClick = {},
                        onLikeClick = {},
                        onMediaOpen = {},
                        onRepostClick = {},
                        onMenuClick = {},
                    )
                }
                OutlinedTextField(
                    value =
                        TextFieldValue(
                            text = uiState.text,
                            selection = TextRange(uiState.cursorPosition),
                        ),
                    minLines = 6,
                    onValueChange = {
                        viewModel.handleMentionQueryChanged(
                            newText = it.text,
                            cursorPosition = it.selection.start,
                        )
                    },
                    label = { Text("Add new post") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = MentionVisualTransformation(),
                )

                if (uiState.showSuggestions) {
                    AddPostSuggestions(
                        suggestions = uiState.suggestions,
                        isLoading = uiState.loading,
                        onSelected = { viewModel.handleActorSelected(it) },
                    )
                }

                Text(
                    text = uiState.charactersLeft.toString(),
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .align(Alignment.End),
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.mediaItems.forEach {
                        item {
                            AsyncImage(
                                model = it.uri,
                                contentDescription = "Image",
                                modifier =
                                    Modifier
                                        .size(140.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }

                uiState.selectedGif?.let {
                    Spacer(modifier = Modifier.padding(8.dp))
                    AsyncImage(
                        model = it.getBestFormat(),
                        contentDescription = "Image",
                        modifier =
                            Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            AddPostBottomBar(
                launchImageGallery = { launchImageGallery = true },
                launchVideoGallery = { launchVideoGallery = true },
                launchGif = { launchGif = true },
                sendPost = { viewModel.onUpload() },
                Modifier
                    .align(Alignment.BottomCenter),
            )

            if (launchGif) {
                TenorSearch(
                    searchResults = uiState.gifSearchResults,
                    searchQuery = {
                        scope.launch {
                            viewModel.onGifSearchQueryChanged(it)
                        }
                    },
                    onGifSelected = {
                        viewModel.onGifSelected(it)
                        launchGif = false
                    },
                    onDismiss = {
                        viewModel.clearGifSearch()
                        launchGif = false
                    },
                )
            }
        }
    }
}
