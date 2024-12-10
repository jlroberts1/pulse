package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.network.api.FeedAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.model.FeedResponse
import com.contexts.cosmic.domain.repository.FeedRepository
import com.contexts.cosmic.exceptions.NetworkError

class FeedRepositoryImpl(private val feedAPI: FeedAPI) : FeedRepository {
    override suspend fun getTimeline(): Response<FeedResponse, NetworkError> {
        return feedAPI.getTimeline()
    }
}
