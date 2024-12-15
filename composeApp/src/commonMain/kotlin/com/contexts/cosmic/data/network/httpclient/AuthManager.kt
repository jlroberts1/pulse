/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.domain.model.AuthState
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.Flow

interface AuthManager {
    suspend fun getTokens(): BearerTokens?

    suspend fun putTokens(token: Token)

    fun getAuthStateFlow(): Flow<AuthState?>

    suspend fun getAuthState(): AuthState?
}
