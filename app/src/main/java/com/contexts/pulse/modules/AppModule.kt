/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.WorkManager
import com.contexts.pulse.domain.media.PlayerPoolManager
import com.contexts.pulse.ui.components.SnackbarDelegate
import org.koin.dsl.module

val appModule =
    module {
        single<ExoPlayer>(createdAtStart = false) {
            ExoPlayer.Builder(get<Context>()).build()
        }
        single<PlayerPoolManager>(createdAtStart = false) {
            PlayerPoolManager(get<Context>())
        }
        single<WorkManager>(createdAtStart = false) {
            WorkManager.getInstance(get<Context>())
        }
        single<SnackbarDelegate>(createdAtStart = false) {
            SnackbarDelegate()
        }
    }
