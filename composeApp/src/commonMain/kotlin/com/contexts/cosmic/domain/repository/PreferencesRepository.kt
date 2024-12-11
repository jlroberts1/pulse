/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.UserInfo
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getTokens(): Flow<BearerTokens?>

    suspend fun clearTokens()

    suspend fun putTokens(token: Token)

    fun getUserInfo(): Flow<UserInfo?>

    suspend fun putUserInfo(userInfo: UserInfo)
}
