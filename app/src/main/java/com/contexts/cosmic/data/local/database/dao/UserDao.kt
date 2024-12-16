/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contexts.cosmic.data.local.database.entities.TokenPair
import com.contexts.cosmic.data.local.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM users LIMIT 1)")
    fun isLoggedIn(): Flow<Boolean>

    @Query("SELECT did, accessJwt, refreshJwt FROM users WHERE did = :did")
    suspend fun getToken(did: String): TokenPair?

    @Query(
        """
        UPDATE users 
        SET accessJwt = :newAccessJwt, 
            refreshJwt = :newRefreshJwt
        WHERE accessJwt = :oldAccessJwt
    """,
    )
    suspend fun updateTokens(
        oldAccessJwt: String,
        newAccessJwt: String,
        newRefreshJwt: String,
    )

    @Query("SELECT * FROM users WHERE did = :did")
    suspend fun getUser(did: String): UserEntity
}
