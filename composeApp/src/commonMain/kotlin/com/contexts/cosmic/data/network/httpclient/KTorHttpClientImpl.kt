package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.response.RefreshSessionResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.path

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
        }.apply {
            // Bluesky returns 400 instead of 401 on expired tokens
            // Ktor only tries to refresh tokens on 400
            // Intercept and attempt to refresh before failing
            this.plugin(HttpSend).intercept { request ->
                val originalCall = execute(request)
                if (originalCall.response.status.value == 400) {
                    refresh()
                    execute(request)
                } else {
                    originalCall
                }
            }
        }
    }

    override suspend fun refresh() {
        authManager.getTokens()?.let { oldTokens ->
            when (
                val response =
                    client.safeRequest<RefreshSessionResponse> {
                        url {
                            method = HttpMethod.Post
                            path("xrpc/com.atproto.server.refreshSession")
                        }
                        header(HttpHeaders.Authorization, "Bearer ${oldTokens.refreshToken}")
                    }
            ) {
                is Response.Success -> {
                    val tokens =
                        Token(
                            accessJwt = response.data.accessJwt,
                            refreshJwt = response.data.refreshJwt,
                        )
                    authManager.putTokens(tokens)
                    BearerTokens(
                        tokens.accessJwt,
                        tokens.refreshJwt,
                    )
                }

                is Response.Error -> {
                    println("Error refreshing token: ${response.error}")
                    null
                }
            }
        }
    }
}
