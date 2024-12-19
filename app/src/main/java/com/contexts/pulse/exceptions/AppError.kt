/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.exceptions

sealed interface AppError {
    val message: String
}

sealed interface NetworkError : AppError {
    enum class Http(override val message: String) : NetworkError {
        REQUEST_TIMEOUT("Request timeout"),
        UNAUTHORIZED("Unauthorized"),
        CONFLICT("Conflict"),
        TOO_MANY_REQUESTS("Too many requests"),
        SERVER_ERROR("Server error"),
    }

    enum class Network(override val message: String) : NetworkError {
        NO_INTERNET_CONNECTION("No internet connection"),
        SERIALIZATION("Serialization"),
    }

    data class Unknown(override val message: String) : NetworkError
}

sealed interface DatabaseError : AppError {
    data class NotFound(override val message: String) : DatabaseError

    data class WriteFailed(override val message: String) : DatabaseError

    data class ReadFailed(override val message: String) : DatabaseError
}
