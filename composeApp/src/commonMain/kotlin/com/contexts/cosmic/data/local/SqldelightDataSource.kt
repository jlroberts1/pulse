package com.contexts.cosmic.data.local

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import com.contexts.cosmic.db.Auth_state
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.domain.model.AuthState
import com.contexts.cosmic.domain.model.User
import com.contexts.cosmic.domain.model.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SqldelightDataSource(
    private val database: Database
) : LocalDataSource {
    override suspend fun getUser(did: String): User? = withContext(Dispatchers.IO) {
        database.userQueries
            .selectUser(did)
            .executeAsOneOrNull()
            ?.toUser()
    }

    override suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        database.userQueries.insertUser(
            did = user.did,
            handle = user.handle,
            displayName = user.displayName,
            description = user.description,
            avatar = user.avatar,
            banner = user.banner,
            followersCount = user.followersCount.toLong(),
            followsCount = user.followsCount.toLong(),
            postsCount = user.postsCount.toLong(),
            indexedAt = user.indexedAt ?: "",
        )
    }

    override suspend fun updateProfile(user: User) = withContext(Dispatchers.IO) {
        database.userQueries.updateProfile(
            handle = user.handle,
            displayName = user.displayName,
            description = user.description,
            avatar = user.avatar,
            banner = user.banner,
            followersCount = user.followersCount.toLong(),
            followsCount = user.followsCount.toLong(),
            postsCount = user.postsCount.toLong(),
            indexedAt = user.indexedAt ?: "",
            did = user.did
        )
    }

    override fun getAuthState(): Flow<Query<Auth_state>> =
        database.userQueries
            .getAuthState()
            .asFlow()

    override suspend fun updateAuthState(authState: AuthState) = withContext(Dispatchers.IO) {
        database.userQueries.upsertAuthState(
            userDid = authState.userDid,
            accessJwt = authState.accessJwt,
            refreshJwt = authState.refreshJwt,
            lastRefreshed = authState.lastRefreshed
        )
    }

    override suspend fun clearAuthState() = withContext(Dispatchers.IO) {
        database.userQueries.clearAuthState()
    }

    override suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        database.userQueries
            .isLoggedIn()
            .executeAsOne()
    }
}