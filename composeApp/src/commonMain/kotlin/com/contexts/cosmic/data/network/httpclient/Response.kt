package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.exceptions.AppError
import com.contexts.cosmic.exceptions.NetworkError
import com.contexts.cosmic.extensions.RequestResult
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

inline fun <T, E : NetworkError> Response<T, E>.onError(action: (E) -> Unit): Response<T, E> {
    return when (this) {
        is Response.Success -> this
        is Response.Error -> {
            action(error)
            this
        }
    }
}

fun <T, R> Flow<T>.updateLocal(
    localDataSource: LocalDataSource,
    transform: (T) -> R,
    saveAction: suspend (LocalDataSource, R) -> Unit,
): Flow<R> =
    map { item ->
        transform(item).also { result ->
            saveAction(localDataSource, result)
        }
    }

suspend fun <T, E : NetworkError, R> Response<T, E>.handleInChannel(
    channelScope: ProducerScope<RequestResult<R, AppError>>,
    transform: (T) -> R,
    saveAction: suspend (R) -> Unit,
) {
    when (this) {
        is Response.Success -> {
            val result = transform(data)
            saveAction(result)
            channelScope.trySend(RequestResult.Success(result))
        }

        is Response.Error -> {
            channelScope.trySend(RequestResult.Error(error))
        }
    }
}

typealias EmptyResult<E> = Response<Unit, E>
