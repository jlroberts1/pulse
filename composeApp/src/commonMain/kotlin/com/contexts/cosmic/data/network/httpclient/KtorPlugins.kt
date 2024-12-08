package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.response.RefreshSessionResponse
import com.contexts.cosmic.domain.repository.PreferencesRepository
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

fun HttpClientConfig<*>.setupContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }
}

fun HttpClientConfig<*>.setupLogging() {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}

fun HttpClientConfig<*>.setupDefaultRequest() {
    install(DefaultRequest) {
        url("https://bsky.social/xrpc")
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}

fun HttpClientConfig<*>.setupAuth(preferencesRepository: PreferencesRepository) {
    install(Auth) {
        bearer {
            loadTokens {
                preferencesRepository.getTokens().firstOrNull()
            }
            refreshTokens {
                preferencesRepository.getTokens().firstOrNull()?.let { oldTokens ->
                    oldTokens.refreshToken?.let {
                        when (
                            val response =
                                client.safeRequest<RefreshSessionResponse> {
                                    markAsRefreshTokenRequest()
                                    url {
                                        method = HttpMethod.Post
                                        path("com.atproto.server.refreshSession")
                                    }
                                    header(HttpHeaders.Authorization, "Bearer $it")
                                }
                        ) {
                            is Response.Success -> {
                                val tokens =
                                    Token(
                                        accessJwt = response.data.accessJwt,
                                        refreshJwt = response.data.refreshJwt,
                                    )
                                preferencesRepository.putTokens(tokens)
                            }

                            is Response.Error -> {
                                println("Error refreshing token: ${response.error}")
                            }
                        }
                    }
                    preferencesRepository.getTokens().firstOrNull()
                }
            }
            sendWithoutRequest { request ->
                when (request.url.pathSegments.last()) {
                    "com.atproto.server.createSession",
                    "com.atproto.server.refreshSession" -> false

                    else -> true
                }
            }
        }
    }
}