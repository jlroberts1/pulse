/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.extensions

sealed interface RequestResult<out T, out E> {
    data object Loading : RequestResult<Nothing, Nothing>

    data class Success<out T>(val data: T) : RequestResult<T, Nothing>

    data class Error<out E>(val error: E) : RequestResult<Nothing, E>
}
