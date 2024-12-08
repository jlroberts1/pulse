package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.datastore.PreferencesDataSource
import com.contexts.cosmic.data.network.model.Token
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

    companion object {
        const val ACCESS_JWT = "access_jwt"
        const val REFRESH_JWT = "refresh_jwt"
    }
}