/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contexts.cosmic.data.local.database.entities.FeedPostEntity

@Dao
interface FeedPostDao {
    @Query("SELECT * FROM feed_posts WHERE feedId = :feedId ORDER BY indexedAt DESC")
    fun getPostsForFeed(feedId: String): PagingSource<Int, FeedPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<FeedPostEntity>)

    @Query("DELETE FROM feed_posts WHERE feedId = :feedUri")
    suspend fun clearFeed(feedUri: String)

    @Query("SELECT * FROM feed_posts WHERE postUri = :uri")
    suspend fun getPost(uri: String): FeedPostEntity?

    @Query(
        """
        UPDATE feed_posts
        SET likeCount = :likeCount,
            repostCount = :repostCount,
            replyCount = :replyCount
        WHERE postUri = :uri
    """,
    )
    suspend fun updateCounts(
        uri: String,
        likeCount: Long,
        repostCount: Long,
        replyCount: Long,
    )

    @Query("SELECT COUNT(*) FROM feed_posts WHERE feedId = :feedId")
    suspend fun getPostsCount(feedId: String): Int

    @Query("UPDATE feed_posts SET isLiked = :isLiked WHERE postUri = :uri")
    suspend fun updateLikeStatus(
        uri: String,
        isLiked: Boolean,
    )

    @Query("UPDATE feed_posts SET isReposted = :isReposted WHERE postUri = :uri")
    suspend fun updateRepostStatus(
        uri: String,
        isReposted: Boolean,
    )
}
