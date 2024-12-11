/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.di

import org.koin.core.context.startKoin

fun initKoin()  {
    startKoin {
        modules(appModule + platformModule + sqlDriverModule)
    }
}
