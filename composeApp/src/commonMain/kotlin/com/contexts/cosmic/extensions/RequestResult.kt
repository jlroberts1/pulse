package com.contexts.cosmic.extensions

sealed interface RequestResult<out T, out E> {
    data object Loading : RequestResult<Nothing, Nothing>
    data class Success<out T>(val data: T) : RequestResult<T, Nothing>
    data class Error<out E>(val error: E) : RequestResult<Nothing, E>
}