/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.local.database.dao.FeedDao
import com.contexts.pulse.data.local.database.dao.RemoteKeysDao
import com.contexts.pulse.domain.repository.FeedRepository
import com.contexts.pulse.modules.AppDispatchers

class TimelineRemoteMediatorFactory(
    private val appDispatchers: AppDispatchers,
    private val feedDao: FeedDao,
    private val feedRepository: FeedRepository,
    private val remoteKeysDao: RemoteKeysDao,
    private val db: PulseDatabase,
) {
    fun create(
        feedId: String,
        feedUri: String,
    ) = TimelineRemoteMediator(
        appDispatchers = appDispatchers,
        feedId = feedId,
        feedUri = feedUri,
        feedDao = feedDao,
        remoteKeysDao = remoteKeysDao,
        feedRepository = feedRepository,
        db = db,
    )
}
