/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.contexts.pulse.domain.model.DidDocument
import com.contexts.pulse.domain.model.User

@Entity(tableName = "users", indices = [Index(value = ["did"], unique = true)])
data class UserEntity(
    @PrimaryKey
    val did: String,
    @Embedded
    val didDoc: DidDocument?,
    val handle: String,
    val email: String?,
    val accessJwt: String,
    val refreshJwt: String,
)

fun User.toUserEntity() =
    UserEntity(
        did = did,
        didDoc = didDoc,
        handle = handle,
        email = email,
        accessJwt = accessJwt,
        refreshJwt = refreshJwt,
    )

data class TokenPair(
    val did: String,
    val accessJwt: String,
    val refreshJwt: String,
)
