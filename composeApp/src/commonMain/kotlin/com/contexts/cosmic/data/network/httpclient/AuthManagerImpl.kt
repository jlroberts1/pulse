package com.contexts.cosmic.data.network.httpclient

import app.cash.sqldelight.coroutines.mapToOne
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.domain.model.AuthState
import com.contexts.cosmic.domain.model.toAuthState
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthManagerImpl(private val localDataSource: LocalDataSource) : AuthManager {
    override suspend fun getTokens(): BearerTokens? = withContext(Dispatchers.IO) {
        val authState =
            localDataSource.getAuthState().mapToOne(Dispatchers.IO).first().toAuthState()
        return@withContext BearerTokens(
            accessToken = authState.accessJwt,
            refreshToken = authState.refreshJwt
        )
    }

    override suspend fun putTokens(token: Token) = withContext(Dispatchers.IO) {
        val authState =
            localDataSource.getAuthState().mapToOne(Dispatchers.IO).first().toAuthState()
        localDataSource.updateAuthState(
            authState.copy(
                accessJwt = token.accessJwt,
                refreshJwt = token.refreshJwt
            )
        )
    }

    override fun getAuthState(): Flow<AuthState?> =
        localDataSource.getAuthState().mapToOne(Dispatchers.IO).map { it.toAuthState() }
}