/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferencesRepositoryImpl(private val preferencesDataSource: PreferencesDataSource) :
    PreferencesRepository {
    override suspend fun updateTheme(theme: Theme) =
        withContext(Dispatchers.IO) {
            preferencesDataSource.editValue(THEME, theme.name)
        }

    override fun getTheme(): Flow<Theme> =
        preferencesDataSource.getValue(THEME, "system", String::class)
            .map { Theme.valueOf(it.toUpperCase(Locale.current)) }
            .flowOn(Dispatchers.IO)

    companion object {
        const val THEME = "theme"
    }
}

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK,
}
