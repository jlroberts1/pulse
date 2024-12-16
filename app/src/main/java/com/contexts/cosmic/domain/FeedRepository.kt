/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain

import app.bsky.feed.GetFeedResponse
import com.contexts.cosmic.data.network.client.Response
import com.contexts.cosmic.exceptions.NetworkError

interface FeedRepository {
    suspend fun getDefaultFeed(): Response<GetFeedResponse, NetworkError>
}
