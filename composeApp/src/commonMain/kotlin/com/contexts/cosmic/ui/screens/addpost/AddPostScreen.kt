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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.contexts.cosmic.util.PermissionCallback
import com.contexts.cosmic.util.PermissionStatus
import com.contexts.cosmic.util.PermissionType
import com.contexts.cosmic.util.createPermissionsManager
import com.contexts.cosmic.util.rememberCameraManager
import com.contexts.cosmic.util.rememberGalleryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddPostScreen() {
    val coroutineScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }
    var postText by remember { mutableStateOf("") }
    var charactersLeft by remember { mutableStateOf(300) }
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
                val bitmap =
                    withContext(Dispatchers.Default) {
                        it?.toImageBitmap()
                    }
                imageBitmap = bitmap
            }
        }

    val galleryManager =
        rememberGalleryManager {
            coroutineScope.launch {
                val bitmap =
                    withContext(Dispatchers.Default) {
                        it?.toImageBitmap()
                    }
                imageBitmap = bitmap
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
        Dialog(
            onDismissRequest = { },
            properties =
                DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(size = 12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Permissions required")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "We need permissions to get media to post",
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp, start = 16.dp),
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                permissionRationalDialog = false
                            },
                        ) {
                            Text(text = "Cancel", textAlign = TextAlign.Center, maxLines = 1)
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                permissionRationalDialog = false
                                launchSetting = true
                            },
                        ) {
                            Text(
                                text = "Launch settings",
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
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
                value = postText,
                minLines = 6,
                onValueChange = {
                    postText = it
                    charactersLeft = 300 - it.length
                },
                label = { Text("Add new post") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = charactersLeft.toString(),
                modifier =
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.End),
            )

            if (imageBitmap != null) {
                Spacer(modifier = Modifier.padding(8.dp))
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "Image",
                    modifier =
                        Modifier.size(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        BottomAppBar(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
            actions = {
                IconButton(onClick = { launchCamera = true }) {
                    Icon(Icons.Default.Camera, "Camera")
                }
                IconButton(onClick = { launchGallery = true }) {
                    Icon(Icons.Default.Photo, "Gallery")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Gif, "Gif")
                }
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(
                    onClick = { },
                    modifier =
                        Modifier
                            .padding(8.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send")
                }
            },
        )
    }
}
