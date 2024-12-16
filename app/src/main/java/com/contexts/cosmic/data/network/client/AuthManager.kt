/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.client

import com.atproto.server.RefreshSessionResponse
import com.contexts.cosmic.data.local.database.dao.UserDao
import com.contexts.cosmic.data.local.database.entities.TokenPair
import com.contexts.cosmic.data.network.requests.RefreshSessionRequest
import com.contexts.cosmic.domain.PreferencesRepository
import com.contexts.cosmic.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow

class AuthManager(
    private val preferencesRepository: PreferencesRepository,
    private val userDao: UserDao,
) {
    fun getCurrentUserFlow(): Flow<String> {
        return preferencesRepository.getCurrentUserFlow()
    }

    suspend fun getToken(did: String): TokenPair? {
        return userDao.getToken(did)
    }

    suspend fun refreshToken(refreshToken: String): Response<RefreshSessionResponse, NetworkError> {
        val httpClient =
            HttpClient(OkHttp) {
                expectSuccess = true
                setupContentNegotiation()
                setupDefaultRequest()
            }
        return httpClient.safeRequest<RefreshSessionResponse> {
            url {
                method = HttpMethod.Post
                path("xrpc/com.atproto.server.refreshSession")
                setBody(RefreshSessionRequest(refreshToken))
            }
        }
    }

    suspend fun updateTokens(
        oldAccessToken: String,
        newAccessToken: String,
        newRefreshToken: String,
    ) {
        userDao.updateTokens(
            oldAccessToken,
            newAccessToken,
            newRefreshToken,
        )
    }
}
