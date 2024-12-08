package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.NetworkError
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.ProfileViewResponse
import com.contexts.cosmic.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val profileAPI: ProfileAPI) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<ProfileViewResponse, NetworkError> {
        return profileAPI.getProfile(actor)
    }

    override suspend fun getMyProfile(myDid: String): Response<ProfileViewResponse, NetworkError> {
        return profileAPI.getMyProfile(myDid)
    }
}