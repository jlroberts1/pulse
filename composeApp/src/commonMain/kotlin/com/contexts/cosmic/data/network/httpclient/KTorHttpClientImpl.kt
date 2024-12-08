package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.domain.repository.PreferencesRepository
import io.ktor.client.HttpClient

class KTorHttpClientImpl(
    private val preferencesRepository: PreferencesRepository,
) : KTorHttpClient {
    override val client: HttpClient by lazy {
        HttpClient {
            expectSuccess = true
            setupContentNegotiation()
            setupLogging()
            setupDefaultRequest()
            setupAuth(preferencesRepository)
        }
    }
}