/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.repository

import com.contexts.pulse.data.network.client.Response
import com.contexts.pulse.data.network.response.TenorResponse
import com.contexts.pulse.exceptions.NetworkError

interface TenorRepository {
    suspend fun searchTenor(query: String): Response<TenorResponse, NetworkError>
}
