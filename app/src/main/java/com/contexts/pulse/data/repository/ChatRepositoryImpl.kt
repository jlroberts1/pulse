/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import chat.bsky.convo.ListConvosQueryParams
import chat.bsky.convo.ListConvosResponse
import com.contexts.pulse.data.local.database.dao.UserDao
import com.contexts.pulse.data.network.api.ChatAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.domain.repository.ChatRepository
import com.contexts.pulse.domain.repository.PreferencesRepository
import com.contexts.pulse.exceptions.NetworkError
import kotlinx.coroutines.flow.first

class ChatRepositoryImpl(
    private val chatAPI: ChatAPI,
    private val userDao: UserDao,
    private val preferencesRepository: PreferencesRepository,
) : ChatRepository {
    override suspend fun listConvos(listConvosQueryParams: ListConvosQueryParams): Response<ListConvosResponse, NetworkError> {
        val current = preferencesRepository.getCurrentUserFlow().first()
        val serviceEndpoint = userDao.getUser(current).didDoc?.service?.first()?.serviceEndpoint
        return chatAPI.listConvos(serviceEndpoint, listConvosQueryParams)
    }
}
