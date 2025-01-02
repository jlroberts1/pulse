/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contexts.pulse.data.local.database.entities.FeedEntity
import com.contexts.pulse.data.local.database.entities.TimelinePostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feedEntities: FeedEntity)

    @Query("SELECT * FROM feeds WHERE userDid = :did ORDER BY id ASC")
    fun getFeedsForUser(did: String): Flow<List<FeedEntity>>

    @Query("SELECT * FROM timeline_posts WHERE feedId = :feedId ORDER BY indexedAt DESC")
    fun getPostsForFeed(feedId: String): PagingSource<Int, TimelinePostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<TimelinePostEntity>)

    @Query("DELETE FROM timeline_posts WHERE feedId = :feedUri")
    suspend fun clearFeed(feedUri: String)

    @Query("UPDATE timeline_posts SET liked = true, likedUri = :likedUri, likeCount = likeCount + 1 WHERE uri = :postUri")
    suspend fun likePost(
        postUri: String,
        likedUri: String,
    )

    @Query("UPDATE timeline_posts SET liked = false, likedUri = null, likeCount = MAX(0, likeCount - 1) WHERE uri = :postUri")
    suspend fun unlikePost(postUri: String)
}
