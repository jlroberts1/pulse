/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.addpost

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.getBestFormat
import com.contexts.cosmic.ui.screens.addpost.composables.AddPostBottomBar
import com.contexts.cosmic.ui.screens.addpost.composables.AddPostSuggestions
import com.contexts.cosmic.ui.screens.addpost.composables.TenorSearch
import com.contexts.cosmic.ui.screens.addpost.composables.rememberGalleryManager
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPostScreen() {
    val viewModel: AddPostViewModel = koinViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchGif by remember { mutableStateOf(value = false) }

    val galleryManager =
        rememberGalleryManager {
            coroutineScope.launch {
                viewModel.onImageSelected(it?.toImageBitmap())
            }
        }

    if (launchGallery) {
        galleryManager.launch()
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
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

            uiState.image?.let {
                Spacer(modifier = Modifier.padding(8.dp))
                Image(
                    bitmap = it,
                    contentDescription = "Image",
                    modifier =
                        Modifier.size(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            uiState.selectedGif?.let {
                Spacer(modifier = Modifier.padding(8.dp))
                AsyncImage(
                    model = it.getBestFormat(),
                    contentDescription = "Image",
                    modifier =
                        Modifier.size(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        AddPostBottomBar(
            launchGallery = { launchGallery = true },
            launchGif = { launchGif = true },
            sendPost = {},
            Modifier
                .align(Alignment.BottomCenter),
        )

        if (launchGif) {
            TenorSearch(
                searchResults = uiState.gifSearchResults,
                searchQuery = {
                    coroutineScope.launch {
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
