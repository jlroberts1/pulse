/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.client

import com.contexts.pulse.data.local.database.entities.TokenPair
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import logcat.logcat
import okhttp3.Interceptor
import okhttp3.Request
typealias OkhttpResponse = okhttp3.Response

class AuthInterceptor(
    private val authManager: AuthManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkhttpResponse {
        val activeUser =
            runBlocking {
                authManager.getCurrentUserFlow().firstOrNull()
            } ?: return chain.proceed(chain.request())

        val token =
            runBlocking {
                authManager.getToken(activeUser)
            } ?: return chain.proceed(chain.request())

        val initialRequest =
            chain.request().newBuilder()
                .addHeader(HttpHeaders.Authorization, "Bearer ${token.accessJwt}")
                .build()

        var response = chain.proceed(initialRequest)

        if (response.code == 400) {
            val responseBody = response.body?.string()
            if (responseBody?.contains("ExpiredToken") == true) {
                response.close()

                response = refreshToken(chain, token, initialRequest)
            }
        }
        return response
    }

    private fun refreshToken(
        chain: Interceptor.Chain,
        token: TokenPair,
        initialRequest: Request,
    ): OkhttpResponse {
        val newToken =
            runBlocking {
                when (val response = authManager.refreshToken(refreshToken = token.refreshJwt)) {
                    is Response.Success -> {
                        authManager.updateTokens(
                            token.accessJwt,
                            response.data.accessJwt,
                            response.data.refreshJwt,
                        )
                        response.data.accessJwt
                    }
                    is Response.Error -> {
                        logcat { "Failed to refresh token, ${response.error.message}" }
                    }
                }
            }

        val newRequest =
            initialRequest.newBuilder()
                .removeHeader(HttpHeaders.Authorization)
                .addHeader(HttpHeaders.Authorization, "Bearer $newToken")
                .build()

        return chain.proceed(newRequest)
    }
}
