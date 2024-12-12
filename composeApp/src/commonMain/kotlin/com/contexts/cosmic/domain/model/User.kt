/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import app.bsky.actor.GetProfileResponse
import sh.christian.ozone.api.model.Timestamp

data class User(
    val did: String,
    val handle: String,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
    val banner: String?,
    val followersCount: Long,
    val followsCount: Long,
    val postsCount: Long,
    val indexedAt: Timestamp?,
)

fun com.contexts.cosmic.db.User.toUser(): User {
    return User(
        did = did,
        handle = handle,
        displayName = displayName,
        description = description,
        avatar = avatar,
        banner = banner,
        followersCount = followersCount,
        followsCount = followsCount,
        postsCount = postsCount,
        indexedAt = indexedAt,
    )
}

fun GetProfileResponse.toUser() =
    User(
        did = this.did.did,
        handle = this.handle.handle,
        displayName = this.displayName,
        description = this.description,
        avatar = this.avatar?.uri,
        banner = this.banner?.uri,
        followersCount = this.followersCount ?: 0,
        followsCount = this.followsCount ?: 0,
        postsCount = this.postsCount ?: 0,
        indexedAt = this.indexedAt,
    )
