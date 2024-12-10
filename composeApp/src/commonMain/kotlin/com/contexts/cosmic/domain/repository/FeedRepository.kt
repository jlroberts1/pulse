package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.model.FeedResponse
import com.contexts.cosmic.exceptions.NetworkError

interface FeedRepository {
    suspend fun getTimeline(): Response<FeedResponse, NetworkError>
}
