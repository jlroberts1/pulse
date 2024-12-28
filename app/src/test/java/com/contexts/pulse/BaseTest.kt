/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse

import com.contexts.pulse.modules.testDispatchersModule
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

open class BaseTest : KoinTest {
    @Before
    fun setupKoin() {
        startKoin {
            modules(testDispatchersModule)
        }
    }

    @After
    fun teardownKoin() {
        stopKoin()
    }
}
