/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contexts.pulse.data.local.database.entities.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: RemoteKeysEntity)

    @Query("DELETE FROM remote_keys WHERE feedId = :feedId")
    suspend fun clearKeysByFeed(feedId: String)

    @Query("SELECT * FROM remote_keys WHERE feedId = :feedId")
    suspend fun getRemoteKeyForFeed(feedId: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAllKeys()
}
