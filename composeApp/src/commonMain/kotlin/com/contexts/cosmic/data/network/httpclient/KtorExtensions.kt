package com.contexts.cosmic.data.network.httpclient

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
        Response.Error(ApiError.NO_INTERNET_CONNECTION)
    } catch (exception: SerializationException) {
        println(exception)
        Response.Error(ApiError.SERIALIZATION)
    } catch (exception: ClientRequestException) {
        println(exception)
        when (exception.response.status.value) {
            401 -> Response.Error(ApiError.UNAUTHORIZED)
            403 -> Response.Error(ApiError.UNAUTHORIZED)
            404 -> Response.Error(ApiError.UNAUTHORIZED)
            408 -> Response.Error(ApiError.REQUEST_TIMEOUT)
            409 -> Response.Error(ApiError.CONFLICT)
            429 -> Response.Error(ApiError.TOO_MANY_REQUESTS)
            in 500..599 -> Response.Error(ApiError.SERVER_ERROR)
            else -> Response.Error(ApiError.UNKNOWN)
        }
    } catch (e: Exception) {
        println(e)
        Response.Error(ApiError.UNKNOWN)
    }

fun HttpClient.invalidateBearerTokens() {
    authProviders
        .filterIsInstance<BearerAuthProvider>()
        .singleOrNull()?.clearToken()
}