/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.client

import com.contexts.pulse.data.local.database.dao.UserDao
import com.contexts.pulse.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountManager(
    private val userDao: UserDao,
    private val preferencesRepository: PreferencesRepository,
    private val scope: CoroutineScope,
) {
    private val _currentPdsUrl = MutableStateFlow<String?>(null)
    val currentPdsUrl = _currentPdsUrl.asStateFlow()

    init {
        scope.launch {
            preferencesRepository.getCurrentUserFlow().collect { did ->
                did?.let {
                    val user = userDao.getUser(did)
                    _currentPdsUrl.update {
                        user.didDoc?.service?.firstOrNull()?.serviceEndpoint
                    }
                }
            }
        }
    }
}
