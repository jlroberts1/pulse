package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.exceptions.NetworkError

interface AuthenticateRepository {
    suspend fun createSession(
        identifier: String,
        password: String,
    ): Response<Unit, NetworkError>
}
