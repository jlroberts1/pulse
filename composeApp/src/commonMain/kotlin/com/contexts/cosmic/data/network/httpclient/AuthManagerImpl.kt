/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.httpclient

import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.domain.model.AuthState
import com.contexts.cosmic.domain.model.toAuthState
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthManagerImpl(private val localDataSource: LocalDataSource) : AuthManager {
    override suspend fun getTokens(): BearerTokens? =
        withContext(Dispatchers.IO) {
            val authState =
                localDataSource.getAuthState().mapToOneOrNull(Dispatchers.IO).firstOrNull()
                    ?.toAuthState()
            authState?.let {
                return@withContext BearerTokens(
                    accessToken = authState.accessJwt,
                    refreshToken = authState.refreshJwt,
                )
            }
        }

    override suspend fun putTokens(token: Token) =
        withContext(Dispatchers.IO) {
            val authState =
                localDataSource.getAuthState().mapToOneOrNull(Dispatchers.IO).firstOrNull()
                    ?.toAuthState()
            authState?.let {
                localDataSource.updateAuthState(
                    authState.copy(
                        accessJwt = token.accessJwt,
                        refreshJwt = token.refreshJwt,
                    ),
                )
            } ?: Unit
        }

    override fun getAuthStateFlow(): Flow<AuthState?> =
        localDataSource.getAuthState()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toAuthState() }

    override suspend fun getAuthState() =
        localDataSource.getAuthState()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toAuthState() }
            .firstOrNull()
}
