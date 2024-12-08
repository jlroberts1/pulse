package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.network.model.Token
import com.contexts.cosmic.data.network.model.UserInfo
import com.contexts.cosmic.domain.repository.PreferencesRepository
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PreferencesRepositoryImpl(private val preferencesDataSource: PreferencesDataSource) :
    PreferencesRepository {
    override fun getTokens(): Flow<BearerTokens?> {
        return combine(
            preferencesDataSource.getValue(ACCESS_JWT, "", String::class),
            preferencesDataSource.getValue(REFRESH_JWT, "", String::class)
        ) { accessToken, refreshToken ->
            BearerTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }

    override suspend fun clearTokens() {
        preferencesDataSource.clear(ACCESS_JWT, String::class)
        preferencesDataSource.clear(REFRESH_JWT, String::class)
    }

    override suspend fun putTokens(token: Token) {
        preferencesDataSource.editValue(ACCESS_JWT, token.accessJwt)
        preferencesDataSource.editValue(REFRESH_JWT, token.refreshJwt)
    }

    override fun getUserInfo(): Flow<UserInfo?> {
        return combine(
            preferencesDataSource.getValue(USER_DID, "", String::class),
            preferencesDataSource.getValue(USER_HANDLE, "", String::class)
        ) { did, handle ->
            UserInfo(
                did = did,
                handle = handle
            )
        }
    }

    override suspend fun putUserInfo(userInfo: UserInfo) {
        preferencesDataSource.editValue(USER_DID, userInfo.did)
        preferencesDataSource.editValue(USER_HANDLE, userInfo.handle)
    }

    companion object {
        const val ACCESS_JWT = "access_jwt"
        const val REFRESH_JWT = "refresh_jwt"

        const val USER_DID = "user_did"
        const val USER_HANDLE = "user_handle"
    }
}