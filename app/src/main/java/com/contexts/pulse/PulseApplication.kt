/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse

import android.app.Application
import com.contexts.pulse.modules.appModule
import com.contexts.pulse.modules.localDataModule
import com.contexts.pulse.modules.networkModule
import com.contexts.pulse.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PulseApplication)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    localDataModule,
                    viewModelModule,
                ),
            )
        }
    }
}
