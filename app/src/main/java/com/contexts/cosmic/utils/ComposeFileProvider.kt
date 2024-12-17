/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.contexts.cosmic.R
import java.io.File
import java.util.Objects

class ComposeFileProvider : FileProvider(
    R.xml.provider_paths,
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val tempFile =
                File.createTempFile(
                    "picture_${System.currentTimeMillis()}",
                    ".png",
                    context.cacheDir,
                ).apply {
                    createNewFile()
                }
            val authority = context.applicationContext.packageName + ".provider"
            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }
    }
}
