/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.cosmic.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AppViewModel(private val userRepository: UserRepository) : ViewModel() {
    val isLoggedIn =
        userRepository.isLoggedIn()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _scaffoldViewState = MutableStateFlow(ScaffoldViewState())
    val scaffoldViewState = _scaffoldViewState.asStateFlow()

    private val _controlsVisibility = MutableStateFlow(1f)
    val controlsVisibility = _controlsVisibility.asStateFlow()

    fun updateControlsVisibility(scrollDelta: Float) {
        _controlsVisibility.update { currentVisibility ->
            if (scrollDelta < 0) {
                (currentVisibility - (-scrollDelta / 100f)).coerceIn(0f, 1f)
            } else {
                (currentVisibility + (scrollDelta / 100f)).coerceIn(0f, 1f)
            }
        }
    }

    fun updateScaffoldViewState(newState: ScaffoldViewState) {
        _scaffoldViewState.value = newState
    }
}
