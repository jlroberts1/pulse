package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.data.network.httpclient.handleInChannel
import com.contexts.cosmic.data.network.model.response.ProfileDTO
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.domain.model.toUser
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ProfileRepositoryImpl(
    private val profileAPI: ProfileAPI,
    private val localDataSource: LocalDataSource,
) : ProfileRepository {
    override suspend fun getProfile(actor: String): Response<ProfileDTO, NetworkError> {
        return profileAPI.getProfile(actor)
    }

    override suspend fun getMyProfile(myDid: String): Flow<RequestResult<User, AppError>> =
        channelFlow {
            trySend(RequestResult.Loading)
            localDataSource.getUser(myDid)?.let { trySend(RequestResult.Success(it)) }
            profileAPI.getMyProfile(myDid)
                .handleInChannel(
                    channelScope = this,
                    transform = { it.toUser() },
                    saveAction = { localDataSource.updateProfile(it) }
                )
        }
}