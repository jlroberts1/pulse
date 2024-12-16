/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import chat.bsky.convo.ListConvosQueryParams
import chat.bsky.convo.ListConvosResponse
import com.contexts.cosmic.data.network.api.ChatAPI
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.domain.repository.ChatRepository
import com.contexts.cosmic.exceptions.NetworkError

class ChatRepositoryImpl(
    private val chatAPI: ChatAPI,
) : ChatRepository {
    override suspend fun listConvos(
        serviceEndpoint: String,
        listConvosQueryParams: ListConvosQueryParams,
    ): Response<ListConvosResponse, NetworkError> {
        return chatAPI.listConvos(serviceEndpoint, listConvosQueryParams)
    }
}
