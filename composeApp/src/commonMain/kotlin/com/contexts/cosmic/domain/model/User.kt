/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.model

import com.contexts.cosmic.data.network.model.response.ProfileDTO

data class User(
    val did: String,
    val handle: String,
    val displayName: String?,
    val description: String?,
    val avatar: String?,
    val banner: String?,
    val followersCount: Int,
    val followsCount: Int,
    val postsCount: Int,
    val indexedAt: String?,
)

fun com.contexts.cosmic.db.User.toUser(): User {
    return User(
        did = did,
        handle = handle,
        displayName = displayName,
        description = description,
        avatar = avatar,
        banner = banner,
        followersCount = followersCount.toInt(),
        followsCount = followsCount.toInt(),
        postsCount = postsCount.toInt(),
        indexedAt = indexedAt,
    )
}

fun ProfileDTO.toUser() =
    User(
        did = did,
        handle = this.handle,
        displayName = this.displayName,
        description = this.description,
        avatar = this.avatar,
        banner = this.banner,
        followersCount = this.followersCount,
        followsCount = this.followsCount,
        postsCount = this.postsCount,
        indexedAt = this.indexedAt ?: "",
    )
