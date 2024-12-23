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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.contexts.pulse.domain.model.Theme
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MediaState(
    val url: String? = null,
)

class AppViewModel(
    private val feedRepository: FeedRepository,
    profileRepository: ProfileRepository,
    preferencesRepository: PreferencesRepository,
    userRepository: UserRepository,
    private val workManager: WorkManager,
) : ViewModel() {
    val theme =
        preferencesRepository.getTheme()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Theme.SYSTEM)

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState = _mediaState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val isLoggedIn =
        userRepository.isLoggedIn()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val currentUser = preferencesRepository.getCurrentUserFlow()

    val profile =
        profileRepository.getMyProfile()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _snackbarMessages = MutableStateFlow<String?>(null)
    val snackbarMessages = _snackbarMessages.asStateFlow()

    init {
        checkAuthState()
        refreshFeeds()
        observeWork()
    }

    private fun observeWork() {
        val workQuery =
            WorkQuery.Builder
                .fromTags(listOf("post_upload"))
                .build()
        workManager.getWorkInfosFlow(workQuery)
            .onEach { workInfos ->
                workInfos.forEach { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val uploadId = workInfo.outputData.getLong("upload_id", -1)
                            if (uploadId != -1L) {
                                _snackbarMessages.update { "Post uploaded successfully!" }
                            }
                        }

                        WorkInfo.State.FAILED -> {
                            val error = workInfo.outputData.getString("error") ?: "Upload failed"
                            _snackbarMessages.update { error }
                        }

                        else -> {}
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun clearSnackbar() {
        _snackbarMessages.update { null }
    }

    private fun checkAuthState() {
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

    private fun refreshFeeds() {
        viewModelScope.launch {
            currentUser.collect { it?.let { feedRepository.refreshFeeds(it) } }
        }
    }

    fun onMediaOpen(url: String) {
        _mediaState.update { it.copy(url = url) }
    }

    fun onMediaDismissed() {
        _mediaState.update { it.copy(url = null) }
    }
}
