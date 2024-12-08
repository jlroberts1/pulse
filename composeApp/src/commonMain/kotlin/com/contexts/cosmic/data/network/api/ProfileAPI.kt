package com.contexts.cosmic.data.network.api

import com.contexts.cosmic.data.network.httpclient.NetworkError
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.safeRequest
import com.contexts.cosmic.data.network.model.response.ProfileViewResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class ProfileAPI(private val client: HttpClient) {
    suspend fun getProfile(actor: String): Response<ProfileViewResponse, NetworkError> {
        return client.safeRequest {
            url {
                method = HttpMethod.Get
                path("xrpc/app.bsky.actor.getProfile")
                parameters.append("actor", actor)
            }
        }
    }

    suspend fun getMyProfile(myDid: String): Response<ProfileViewResponse, NetworkError> {
        return getProfile(myDid)
    }
}