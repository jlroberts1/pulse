package com.contexts.cosmic.data.network.httpclient

import com.contexts.cosmic.exceptions.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

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

fun HttpClient.invalidateBearerTokens() {
    authProviders
        .filterIsInstance<BearerAuthProvider>()
        .singleOrNull()?.clearToken()
}