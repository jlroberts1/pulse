package com.contexts.cosmic.data.network.httpclient

import io.ktor.client.HttpClient

interface KTorHttpClient {
    val client: HttpClient

    suspend fun refresh()
}
