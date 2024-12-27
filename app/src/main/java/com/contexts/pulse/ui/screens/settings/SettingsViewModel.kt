/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.pulse.domain.model.Theme
import com.contexts.pulse.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val theme =
        preferencesRepository.getTheme()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Theme.SYSTEM)

    fun updateTheme(newTheme: Theme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(newTheme)
        }
    }
}
