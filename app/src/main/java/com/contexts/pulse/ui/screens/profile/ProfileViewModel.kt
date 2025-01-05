/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.contexts.pulse.data.network.client.onError
import com.contexts.pulse.data.network.client.onSuccess
import com.contexts.pulse.data.repository.RequestResult
import com.contexts.pulse.domain.model.FullProfile
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.toProfile
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.domain.repository.ProfileRepository
import com.contexts.pulse.ui.screens.main.NavigationRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: FullProfile? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val userDid: String? =
        savedStateHandle[NavigationRoutes.Authenticated.ViewProfile.ARG_USER_DID]

    private val _feedState = MutableStateFlow<PagingData<TimelinePost>>(PagingData.empty())
    val feedState = _feedState.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val actor = userDid ?: preferencesRepository.getCurrentUser() ?: return@launch
            profileRepository.getProfile(actor).onSuccess { response ->
                _uiState.update { it.copy(profile = response.toProfile(), loading = false) }
            }.onError { error ->
                _uiState.update { it.copy(loading = false, error = error.message) }
            }
            when (val result = profileRepository.getProfileFeed(actor)) {
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
