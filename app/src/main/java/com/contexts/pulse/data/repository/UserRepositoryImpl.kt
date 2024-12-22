/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import com.contexts.pulse.data.local.database.dao.UserDao
import com.contexts.pulse.data.local.database.entities.toUserEntity
import com.contexts.pulse.domain.model.User
import com.contexts.pulse.domain.repository.UserRepository
import com.contexts.pulse.modules.AppDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val appDispatchers: AppDispatchers,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun insertUser(user: User) =
        withContext(appDispatchers.io) {
            userDao.insertUser(user.toUserEntity())
        }

    override fun isLoggedIn(): Flow<Boolean> {
        return userDao.isLoggedIn().flowOn(appDispatchers.io)
    }
}
