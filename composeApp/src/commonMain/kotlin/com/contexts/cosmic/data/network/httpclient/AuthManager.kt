package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.domain.model.AuthState
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.Flow

interface AuthManager {
    suspend fun getTokens(): BearerTokens?

    suspend fun putTokens(token: Token)

    fun getAuthState(): Flow<AuthState?>
}
