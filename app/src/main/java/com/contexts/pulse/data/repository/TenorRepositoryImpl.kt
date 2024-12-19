/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.repository

import com.contexts.pulse.data.network.api.TenorAPI
import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.response.TenorResponse
import com.contexts.pulse.domain.repository.TenorRepository
import com.contexts.pulse.exceptions.NetworkError

class TenorRepositoryImpl(private val tenorAPI: TenorAPI) : TenorRepository {
    override suspend fun searchTenor(query: String): Response<TenorResponse, NetworkError> {
        return tenorAPI.searchTenor(query)
    }
}
