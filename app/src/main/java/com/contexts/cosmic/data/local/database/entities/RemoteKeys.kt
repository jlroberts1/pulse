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
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "remote_keys",
    indices = [
        Index(value = ["feedId"]),
    ],
)
data class RemoteKeys(
    @PrimaryKey
    val feedId: String,
    val prevKey: String?,
    val nextKey: String?,
)
