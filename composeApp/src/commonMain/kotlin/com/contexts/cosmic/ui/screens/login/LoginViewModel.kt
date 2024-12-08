package com.contexts.cosmic.ui.screens.login

import androidx.lifecycle.ViewModel
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.toErrorMessage
import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.UserInfo
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val success: Boolean = false,
    val loading: Boolean = false,
)

class LoginViewModel(
    private val authenticateRepository: AuthenticateRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val state = _uiState.asStateFlow()

    suspend fun login(identifier: String, password: String) {
        _uiState.update { it.copy(loading = true) }
        when (val response = authenticateRepository.createSession(identifier, password)) {
            is Response.Success -> {
                preferencesRepository.putTokens(
                    Token(
                        accessJwt = response.data.accessJwt,
                        refreshJwt = response.data.refreshJwt
                    )
                )
                preferencesRepository.putUserInfo(
                    UserInfo(
                        did = response.data.did,
                        handle = response.data.handle
                    )
                )
                _uiState.update { it.copy(success = true, loading = false) }
            }

            is Response.Error -> {
                print(response.error.toErrorMessage())
            }
        }
    }
}