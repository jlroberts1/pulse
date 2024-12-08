package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.NetworkError
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.CreateSessionResponse

interface AuthenticateRepository {
    suspend fun createSession(
        identifier: String,
        password: String,
    ): Response<CreateSessionResponse, NetworkError>

    suspend fun clearToken()
}