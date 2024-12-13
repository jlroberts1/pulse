/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return PermissionsManager(callback)
}

actual class PermissionsManager actual constructor(private val callback: PermissionCallback) :
    PermissionHandler {
        @Composable
        override fun askPermission(permission: PermissionType) {
            when (permission) {
                PermissionType.CAMERA -> {
                    val status: AVAuthorizationStatus =
                        remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                    askCameraPermission(status, permission, callback)
                }

                PermissionType.GALLERY -> {
                    val status: PHAuthorizationStatus =
                        remember { PHPhotoLibrary.authorizationStatus() }
                    askGalleryPermission(status, permission, callback)
                }
            }
        }

        private fun askCameraPermission(
            status: AVAuthorizationStatus,
            permission: PermissionType,
            callback: PermissionCallback,
        ) {
            when (status) {
                AVAuthorizationStatusAuthorized -> {
                    callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                }

                AVAuthorizationStatusNotDetermined -> {
                    return AVCaptureDevice.Companion.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
                        if (isGranted) {
                            callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                        } else {
                            callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                        }
                    }
                }

                AVAuthorizationStatusDenied -> {
                    callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                }

                else -> error("unknown camera status $status")
            }
        }

        private fun askGalleryPermission(
            status: PHAuthorizationStatus,
            permission: PermissionType,
            callback: PermissionCallback,
        ) {
            when (status) {
                PHAuthorizationStatusAuthorized -> {
                    callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                }

                PHAuthorizationStatusNotDetermined -> {
                    PHPhotoLibrary.Companion.requestAuthorization { newStatus ->
                        askGalleryPermission(newStatus, permission, callback)
                    }
                }

                PHAuthorizationStatusDenied -> {
                    callback.onPermissionStatus(
                        permission,
                        PermissionStatus.DENIED,
                    )
                }

                else -> error("unknown gallery status $status")
            }
        }

        @Composable
        override fun isPermissionGranted(permission: PermissionType): Boolean {
            return when (permission) {
                PermissionType.CAMERA -> {
                    val status: AVAuthorizationStatus =
                        remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                    status == AVAuthorizationStatusAuthorized
                }

                PermissionType.GALLERY -> {
                    val status: PHAuthorizationStatus =
                        remember { PHPhotoLibrary.authorizationStatus() }
                    status == PHAuthorizationStatusAuthorized
                }
            }
        }

        @Composable
        override fun launchSettings() {
            NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
                UIApplication.sharedApplication.openURL(it)
            }
        }
    }
