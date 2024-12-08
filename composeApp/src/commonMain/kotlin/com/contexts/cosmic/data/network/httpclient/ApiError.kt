package com.contexts.cosmic.data.network.httpclient

interface NetworkError

enum class ApiError : NetworkError {
    REQUEST_TIMEOUT,
    UNAUTHORIZED,
    CONFLICT,
    TOO_MANY_REQUESTS,
    NO_INTERNET_CONNECTION,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN,
}

fun NetworkError.toErrorMessage(): String {
    return when (this) {
        ApiError.REQUEST_TIMEOUT -> "Request Timeout"
        ApiError.UNAUTHORIZED -> "Unauthorized"
        ApiError.CONFLICT -> "Conflict"
        ApiError.TOO_MANY_REQUESTS -> "Too Many Requests"
        ApiError.NO_INTERNET_CONNECTION -> "No Internet Connection"
        ApiError.SERVER_ERROR -> "Server Error"
        ApiError.SERIALIZATION -> "Serialization Error"
        ApiError.UNKNOWN -> "Unknown Error"
        else -> "Unknown Error"
    }
}