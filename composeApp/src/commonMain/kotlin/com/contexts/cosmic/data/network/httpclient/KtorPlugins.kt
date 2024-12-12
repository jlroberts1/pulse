/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.response.RefreshSessionResponse
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
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
import kotlinx.serialization.json.Json
import sh.christian.ozone.api.xrpc.XrpcSerializersModule

fun HttpClientConfig<*>.setupContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                classDiscriminator = "${'$'}type"
                serializersModule = XrpcSerializersModule
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
        url("https://bsky.social")
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}

fun HttpClientConfig<*>.setupAuth(authManager: AuthManager) {
    install(Auth) {
        bearer {
            loadTokens {
                val token = authManager.getTokens()
                println("$token")
                token
            }
            refreshTokens {
                authManager.getTokens()?.let { oldTokens ->
                    when (
                        val response =
                            client.safeRequest<RefreshSessionResponse> {
                                markAsRefreshTokenRequest()
                                url {
                                    method = HttpMethod.Post
                                    path("xrpc/com.atproto.server.refreshSession")
                                }
                                header(
                                    HttpHeaders.Authorization,
                                    "Bearer ${oldTokens.refreshToken}",
                                )
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
            sendWithoutRequest { request ->
                when (request.url.pathSegments.last()) {
                    "xrpc/com.atproto.server.createSession",
                    "xrpc/com.atproto.server.refreshSession",
                    -> false

                    else -> true
                }
            }
        }
    }
}
