/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.datastore

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

expect interface PreferencesDataSource {
    fun <T : Any> getValue(
        key: String,
        defaultValue: T,
        type: KClass<T>,
    ): Flow<T>

    suspend fun <T : Any> editValue(
        key: String,
        value: T,
    )

    suspend fun <T : Any> clear(
        key: String,
        type: KClass<T>,
    )
}
