package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.httpclient.NetworkError
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.CreateSessionResponse
import com.contexts.cosmic.domain.repository.AuthenticateRepository

class AuthenticateRepositoryImpl(private val api: AuthenticateAPI) : AuthenticateRepository {
    override suspend fun createSession(
        identifier: String,
        password: String
    ): Response<CreateSessionResponse, NetworkError> {
        return api.createSession(identifier, password)
    }

    override suspend fun clearToken() {
        api.clearToken()
    }
}