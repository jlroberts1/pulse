package com.contexts.cosmic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexts.cosmic.data.network.httpclient.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthenticationState {
    data object Loading : AuthenticationState

    data object Authenticated : AuthenticationState

    data object Unauthenticated : AuthenticationState
}

class MainViewModel(
    private val authManager: AuthManager,
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthenticationState>(AuthenticationState.Loading)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.getAuthState().collect { authState ->
                _authState.value =
                    when (authState) {
                        null -> AuthenticationState.Unauthenticated
                        else -> AuthenticationState.Authenticated
                    }
            }
        }
    }
}
