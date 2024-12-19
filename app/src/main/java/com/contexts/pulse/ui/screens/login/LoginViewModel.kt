/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.login

import androidx.lifecycle.ViewModel
import com.atproto.server.CreateSessionRequest
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.AuthenticateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val success: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

class LoginViewModel(
    private val authenticateRepository: AuthenticateRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun login(
        identifier: String,
        password: String,
    ) {
        _uiState.update { it.copy(loading = true) }
        when (val response = authenticateRepository.createSession(CreateSessionRequest(identifier, password))) {
            is Response.Success -> {
                _uiState.update { it.copy(success = true, loading = false) }
            }

            is Response.Error -> {
                _uiState.update { it.copy(error = response.error.message, loading = false) }
            }
        }
    }
}
