package com.contexts.cosmic

sealed class AppState {
    data object Loading : AppState()

    data object Authenticated : AppState()

    data object Unauthenticated : AppState()
}
