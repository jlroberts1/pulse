/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.pulse.domain.model.Theme
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MediaState(
    val url: String? = null,
)

class AppViewModel(
    profileRepository: ProfileRepository,
    preferencesRepository: PreferencesRepository,
    userRepository: UserRepository,
) : ViewModel() {
    val theme =
        preferencesRepository.getTheme()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Theme.SYSTEM)

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState = _mediaState.asStateFlow()

    private val _scaffoldViewState = MutableStateFlow(ScaffoldViewState())
    val scaffoldViewState = _scaffoldViewState.asStateFlow()

    private val _controlsVisibility = MutableStateFlow(1f)
    val controlsVisibility = _controlsVisibility.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val isLoggedIn =
        userRepository.isLoggedIn()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val profile =
        profileRepository.getMyProfile()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    init {
        viewModelScope.launch {
            isLoggedIn.collect { isLoggedInState ->
                isLoggedInState?.let { loggedIn ->
                    _navigationEvent.emit(
                        if (loggedIn) {
                            NavigationRoutes.Authenticated.NavigationRoute.route
                        } else {
                            NavigationRoutes.Unauthenticated.NavigationRoute.route
                        },
                    )
                }
            }
        }
    }

    fun updateControlsVisibility(scrollDelta: Float) {
        _controlsVisibility.update { currentVisibility ->
            if (scrollDelta < 0) {
                (currentVisibility - (-scrollDelta / 100f)).coerceIn(0f, 1f)
            } else {
                (currentVisibility + (scrollDelta / 100f)).coerceIn(0f, 1f)
            }
        }
    }

    fun onMediaOpen(url: String) {
        _mediaState.update { it.copy(url = url) }
    }

    fun onMediaDismissed() {
        _mediaState.update { it.copy(url = null) }
    }

    fun updateScaffoldViewState(newState: ScaffoldViewState) {
        _scaffoldViewState.value = newState
    }
}
