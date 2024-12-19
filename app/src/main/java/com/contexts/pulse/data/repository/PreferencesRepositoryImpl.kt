/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.contexts.pulse.domain.model.Theme
import com.contexts.pulse.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class PreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {
    override suspend fun updateCurrentUser(did: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER] = did
        }
    }

    override suspend fun getCurrentUser(): String? {
        return try {
            dataStore.data
                .map { preferences -> preferences[CURRENT_USER] }
                .mapNotNull { it }
                .first()
        } catch (e: Exception) {
            null
        }
    }

    override fun getCurrentUserFlow(): Flow<String?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[CURRENT_USER]
            }
            .flowOn(Dispatchers.IO)

    override suspend fun updateTheme(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme.name
        }
    }

    override fun getTheme(): Flow<Theme> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                Theme.fromString(preferences[THEME])
            }.flowOn(Dispatchers.IO)

    override suspend fun updateUnreadCount(count: Long) {
        dataStore.edit { preferences ->
            preferences[UNREAD_COUNT] = count
        }
    }

    override fun getUnreadCount(): Flow<Long> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[UNREAD_COUNT] ?: 0L
            }.flowOn(Dispatchers.IO)

    companion object {
        val CURRENT_USER = stringPreferencesKey("current_user")
        val THEME = stringPreferencesKey("theme")
        val UNREAD_COUNT = longPreferencesKey("unread_count")
    }
}
