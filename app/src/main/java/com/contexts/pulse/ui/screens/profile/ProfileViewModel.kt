/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.contexts.pulse.data.repository.RequestResult
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _feedState = MutableStateFlow<PagingData<TimelinePost>>(PagingData.empty())
    val feedState = _feedState.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    val profile =
        profileRepository.getMyProfile()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    init {
        viewModelScope.launch {
            when (val result = profileRepository.getProfileFeed()) {
                is RequestResult.Success -> {
                    result.data
                        .cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _feedState.update { pagingData }
                        }
                }
                is RequestResult.NoCurrentUser -> {
                    _uiState.update { it.copy(error = "No current user") }
                }
            }
        }
    }
}
