/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["did"],
            childColumns = ["userDid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["userDid"], unique = true),
    ],
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userDid: String,
    val handle: String,
    val displayName: String?,
    val banner: String?,
    val avatar: String?,
    val description: String?,
    val followersCount: Long?,
    val followsCount: Long?,
    val postsCount: Long?,
)
