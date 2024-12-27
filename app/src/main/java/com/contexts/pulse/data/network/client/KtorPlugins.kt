/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.data.network.client

import com.contexts.pulse.domain.model.ImageEmbed
import com.contexts.pulse.domain.model.PostEmbed
import com.contexts.pulse.domain.model.VideoEmbed
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import sh.christian.ozone.api.xrpc.XrpcSerializersModule

fun HttpClientConfig<*>.setupContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                classDiscriminator = "\$type"
                serializersModule = XrpcSerializersModule
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule =
                    SerializersModule {
                        polymorphic(PostEmbed::class) {
                            subclass(ImageEmbed::class, ImageEmbed.serializer())
                            subclass(VideoEmbed::class, VideoEmbed.serializer())
                        }
                    }
            },
        )
    }
}

fun HttpClientConfig<*>.setupDefaultRequest() {
    install(DefaultRequest) {
        url("https://bsky.social")
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}
