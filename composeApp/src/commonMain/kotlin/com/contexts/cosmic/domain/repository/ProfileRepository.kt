package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.NetworkError
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.ProfileViewResponse

interface ProfileRepository {
    suspend fun getProfile(actor: String): Response<ProfileViewResponse, NetworkError>
    suspend fun getMyProfile(myDid: String): Response<ProfileViewResponse, NetworkError>
}