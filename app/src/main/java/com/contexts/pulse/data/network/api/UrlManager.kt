/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

object UrlManager {
    private val _currentPdsUrl = MutableStateFlow<String?>(null)

    suspend fun getPdsUrl(): String =
        withContext(Dispatchers.IO) {
            _currentPdsUrl.first { it != null }!!
        }

    fun updatePdsUrl(endpoint: String?) {
        _currentPdsUrl.update { endpoint ?: "https://bsky.social" }
    }
}
