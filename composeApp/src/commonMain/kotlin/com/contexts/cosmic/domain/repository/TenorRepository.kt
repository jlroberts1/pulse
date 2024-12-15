/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.domain.repository

import com.contexts.cosmic.data.network.httpclient.Response
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.ui.screens.addpost.composables.TenorResponse

interface TenorRepository {
    suspend fun searchTenor(query: String): Response<TenorResponse, NetworkError>
}
