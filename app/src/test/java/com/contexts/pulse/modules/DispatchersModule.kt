/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import kotlinx.coroutines.test.StandardTestDispatcher
import org.koin.dsl.module

val testDispatchersModule =
    module {
        single {
            val testDispatcher = StandardTestDispatcher()
            AppDispatchers(
                io = testDispatcher,
                default = testDispatcher,
                main = testDispatcher,
            )
        }
    }
