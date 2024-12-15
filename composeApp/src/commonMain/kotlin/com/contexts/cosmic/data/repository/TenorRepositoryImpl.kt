/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.repository

import com.contexts.cosmic.data.network.api.TenorAPI
import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.domain.repository.TenorRepository
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.ui.screens.addpost.composables.TenorResponse

class TenorRepositoryImpl(private val tenorAPI: TenorAPI) : TenorRepository {
    override suspend fun searchTenor(query: String): Response<TenorResponse, NetworkError> {
        return tenorAPI.searchTenor(query)
    }
}
