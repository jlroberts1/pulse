/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import app.cash.turbine.test
import com.contexts.cosmic.domain.model.Theme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PreferencesRepositoryImplTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: PreferencesRepositoryImpl
    private val testPreferences = mockk<Preferences>()

    @Before
    fun setup() {
        dataStore = mockk()
        repository = PreferencesRepositoryImpl(dataStore)
    }

    @Test
    fun `getCurrentUserFlow returns correct value`() =
        runTest {
            val expectedDid = "test-did"
            every { testPreferences[PreferencesRepositoryImpl.CURRENT_USER] } returns expectedDid
            every { dataStore.data } returns flowOf(testPreferences)

            repository.getCurrentUserFlow().test {
                assertEquals(expectedDid, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getCurrentUserFlow handles IOException`() =
        runTest {
            every { dataStore.data } returns flowOf(emptyPreferences())

            repository.getCurrentUserFlow().test {
                assertEquals("", awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `updateCurrentUser updates preference`() =
        runTest {
            val testDid = "test-did"
            coEvery { dataStore.updateData(any()) } returns mockk()

            repository.updateCurrentUser(testDid)

            coVerify { dataStore.updateData(any()) }
        }

    @Test
    fun `getTheme returns correct theme`() =
        runTest {
            val expectedTheme = Theme.DARK
            every { testPreferences[PreferencesRepositoryImpl.THEME] } returns expectedTheme.name
            every { dataStore.data } returns flowOf(testPreferences)

            repository.getTheme().test {
                assertEquals(expectedTheme, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `updateTheme updates preference`() =
        runTest {
            coEvery { dataStore.updateData(any()) } returns testPreferences

            repository.updateTheme(Theme.DARK)

            coVerify { dataStore.updateData(any()) }
        }

    @Test
    fun `getUnreadCount returns correct count`() =
        runTest {
            val expectedCount = 5L
            every { testPreferences[PreferencesRepositoryImpl.UNREAD_COUNT] } returns expectedCount
            every { dataStore.data } returns flowOf(testPreferences)

            repository.getUnreadCount().test {
                assertEquals(expectedCount, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `getUnreadCount returns 0 when no value set`() =
        runTest {
            every { testPreferences[PreferencesRepositoryImpl.UNREAD_COUNT] } returns null
            every { dataStore.data } returns flowOf(testPreferences)

            repository.getUnreadCount().test {
                assertEquals(0L, awaitItem())
                awaitComplete()
            }
        }
}
