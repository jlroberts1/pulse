/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.client

import com.contexts.pulse.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException

suspend inline fun <reified T> HttpClient.safeRequest(block: HttpRequestBuilder.() -> Unit): Response<T, NetworkError> =
    try {
        val response = request { block() }
        Response.Success<T>(response.body())
    } catch (exception: UnresolvedAddressException) {
        Response.Error(NetworkError.Network.NO_INTERNET_CONNECTION)
    } catch (exception: SerializationException) {
        println(exception)
        Response.Error(NetworkError.Network.SERIALIZATION)
    } catch (exception: ClientRequestException) {
        println(exception)
        when (exception.response.status.value) {
            400 -> Response.Error(NetworkError.Http.UNAUTHORIZED)
            401 -> Response.Error(NetworkError.Http.UNAUTHORIZED)
            403 -> Response.Error(NetworkError.Http.UNAUTHORIZED)
            404 -> Response.Error(NetworkError.Http.UNAUTHORIZED)
            408 -> Response.Error(NetworkError.Http.REQUEST_TIMEOUT)
            409 -> Response.Error(NetworkError.Http.CONFLICT)
            429 -> Response.Error(NetworkError.Http.TOO_MANY_REQUESTS)
            in 500..599 -> Response.Error(NetworkError.Http.SERVER_ERROR)
            else -> Response.Error(NetworkError.Unknown(exception.message))
        }
    } catch (e: Exception) {
        println(e)
        Response.Error(NetworkError.Unknown(e.message ?: "Unknown error"))
    }

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

typealias EmptyResult<E> = Response<Unit, E>
