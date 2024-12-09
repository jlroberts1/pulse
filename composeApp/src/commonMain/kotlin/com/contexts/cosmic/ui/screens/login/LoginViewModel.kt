package com.contexts.cosmic.ui.screens.login

import androidx.lifecycle.ViewModel
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.AuthenticateRepository
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
        when (val response = authenticateRepository.createSession(identifier, password)) {
            is Response.Success -> {
                _uiState.update { it.copy(success = true, loading = false) }
            }

            is Response.Error -> {
                _uiState.update { it.copy(error = response.error.message, loading = false) }
            }
        }
    }
}
