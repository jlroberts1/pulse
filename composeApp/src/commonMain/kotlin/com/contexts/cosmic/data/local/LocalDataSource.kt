/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local

import app.cash.sqldelight.Query
import com.contexts.cosmic.db.Auth_state
import com.contexts.cosmic.domain.model.AuthState
import com.contexts.cosmic.domain.model.User
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getUser(did: String): User?

    suspend fun insertUser(user: User)

    suspend fun updateProfile(user: User)

    fun getAuthState(): Flow<Query<Auth_state>>

    suspend fun updateAuthState(authState: AuthState)

    suspend fun clearAuthState()

    suspend fun isLoggedIn(): Boolean
}
