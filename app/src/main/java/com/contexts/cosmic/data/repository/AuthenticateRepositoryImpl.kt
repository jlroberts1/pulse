/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.data.network.client.onSuccess
import com.contexts.cosmic.domain.AuthenticateRepository
import com.contexts.cosmic.domain.PreferencesRepository
import com.contexts.cosmic.domain.UserRepository
import com.contexts.cosmic.domain.model.toUser
import com.contexts.cosmic.exceptions.NetworkError

class AuthenticateRepositoryImpl(
    private val api: AuthenticateAPI,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) : AuthenticateRepository {
    override suspend fun createSession(createSessionRequest: CreateSessionRequest): Response<CreateSessionResponse, NetworkError> {
        return api.createSession(createSessionRequest)
            .onSuccess {
                preferencesRepository.updateCurrentUser(it.did.did)
                userRepository.insertUser(it.toUser())
            }
    }
}
