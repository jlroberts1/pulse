package com.contexts.cosmic.data.network.httpclient

import io.ktor.client.HttpClient

class KTorHttpClientImpl(
    private val authManager: AuthManager,
) : KTorHttpClient {
    override val client: HttpClient by lazy {
        HttpClient {
            expectSuccess = true
            setupContentNegotiation()
            setupLogging()
            setupDefaultRequest()
            setupAuth(authManager)
        }
    }
}
