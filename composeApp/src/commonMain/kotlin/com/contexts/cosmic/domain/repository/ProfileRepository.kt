package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.model.response.ProfileDTO
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(actor: String): Response<ProfileDTO, NetworkError>
    suspend fun getMyProfile(myDid: String): Flow<RequestResult<User, AppError>>
}