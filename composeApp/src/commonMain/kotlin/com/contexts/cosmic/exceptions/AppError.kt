package com.contexts.cosmic.exceptions

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
