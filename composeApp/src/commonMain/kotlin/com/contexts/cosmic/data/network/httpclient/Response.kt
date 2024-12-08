package com.contexts.cosmic.data.network.httpclient

sealed interface Response<out D, out E : NetworkError> {
    data class Success<out D>(val data: D) : Response<D, Nothing>

    data class Error<out E : NetworkError>(val error: E) : Response<Nothing, E>
}

inline fun <T, E : NetworkError, R> Response<T, E>.map(map: (T) -> R): Response<R, E> =
    when (this) {
        is Response.Success -> Response.Success(map(data))
        is Response.Error -> Response.Error(error)
    }

fun <T, E : NetworkError> Response<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

inline fun <T, E : NetworkError> Response<T, E>.onSuccess(action: (T) -> Unit): Response<T, E> {
    return when (this) {
        is Response.Success -> {
            action(data)
            this
        }

        is Response.Error -> this
    }
}

typealias EmptyResult<E> = Response<Unit, E>