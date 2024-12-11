/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.asEmptyDataResult
import com.contexts.cosmic.data.network.httpclient.map
import com.contexts.cosmic.data.network.httpclient.onSuccess
import com.contexts.cosmic.domain.model.AuthState
import com.contexts.cosmic.domain.model.toUser
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.exceptions.NetworkError
import kotlinx.datetime.Clock

class AuthenticateRepositoryImpl(
    private val authApi: AuthenticateAPI,
    private val profileApi: ProfileAPI,
    private val localDataSource: LocalDataSource,
) : AuthenticateRepository {
    override suspend fun createSession(
        identifier: String,
        password: String,
    ): Response<Unit, NetworkError> {
        return authApi.createSession(identifier, password)
            .onSuccess { response ->
                localDataSource.updateAuthState(
                    AuthState(
                        userDid = response.did,
                        accessJwt = response.accessJwt,
                        refreshJwt = response.refreshJwt,
                        lastRefreshed = Clock.System.now().toString(),
                    ),
                )
            }
            .map { response ->
                profileApi.getProfile(response.did)
                    .onSuccess { userProfile ->
                        localDataSource.insertUser(userProfile.toUser())
                    }
            }
            .asEmptyDataResult()
    }
}
