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
import com.contexts.cosmic.ui.screens.addpost.composables.AddPostBottomBar
import com.contexts.cosmic.ui.screens.addpost.composables.AddPostPermissionRationale
import com.contexts.cosmic.ui.screens.addpost.composables.AddPostSuggestions
import com.contexts.cosmic.util.PermissionCallback
import com.contexts.cosmic.util.PermissionStatus
import com.contexts.cosmic.util.PermissionType
import com.contexts.cosmic.util.createPermissionsManager
import com.contexts.cosmic.util.rememberCameraManager
import com.contexts.cosmic.util.rememberGalleryManager
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPostScreen() {
    val viewModel: AddPostViewModel = koinViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }

    val permissionsManager =
        createPermissionsManager(
            object : PermissionCallback {
                override fun onPermissionStatus(
                    permissionType: PermissionType,
                    status: PermissionStatus,
                ) {
                    when (status) {
                        PermissionStatus.GRANTED -> {
                            when (permissionType) {
                                PermissionType.CAMERA -> launchCamera = true
                                PermissionType.GALLERY -> launchGallery = true
                            }
                        }

                        else -> {
                            permissionRationalDialog = true
                        }
                    }
                }
            },
        )

    val cameraManager =
        rememberCameraManager {
            coroutineScope.launch {
                viewModel.onImageSelected(it?.toImageBitmap())
            }
        }

    val galleryManager =
        rememberGalleryManager {
            coroutineScope.launch {
                viewModel.onImageSelected(it?.toImageBitmap())
            }
        }

    if (launchGallery) {
        if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
            galleryManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.GALLERY)
        }
        launchGallery = false
    }

    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }

    if (launchSetting) {
        permissionsManager.launchSettings()
        launchSetting = false
    }

    if (permissionRationalDialog) {
        AddPostPermissionRationale(
            onDismiss = { permissionRationalDialog = false },
            launchSettings = { launchSetting = true },
        )
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
                    viewModel.handleTextChanged(
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
                    isLoading = uiState.isLoading,
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
        }

        AddPostBottomBar(
            launchCamera = { launchCamera = true },
            launchGallery = { launchGallery = true },
            sendPost = {},
            Modifier
                .align(Alignment.BottomCenter),
        )
    }
}
