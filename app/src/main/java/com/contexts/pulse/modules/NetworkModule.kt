/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.contexts.pulse.BuildConfig
import com.contexts.pulse.data.local.database.PulseDatabase
import com.contexts.pulse.data.network.client.AuthInterceptor
import com.contexts.pulse.data.network.client.AuthManager
import com.contexts.pulse.data.network.client.setupContentNegotiation
import com.contexts.pulse.data.network.client.setupDefaultRequest
import com.contexts.pulse.domain.repository.PreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

val networkModule =
    module {
        single {
            val chuckerCollector =
                ChuckerCollector(
                    context = get(),
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_DAY,
                )
            ChuckerInterceptor.Builder(get())
                .collector(chuckerCollector)
                .maxContentLength(250_000L)
                .alwaysReadResponseBody(true)
                .createShortcut(true)
                .build()
        }
        single {
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }
        }
        single(createdAtStart = false) {
            AuthManager(get<PreferencesRepository>(), get<PulseDatabase>())
        }
        single(createdAtStart = false) {
            AuthInterceptor(get<AuthManager>())
        }
        single(createdAtStart = false) {
            OkHttpClient.Builder()
                .addInterceptor(get<AuthInterceptor>())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(get<ChuckerInterceptor>())
                .build()
        }
        single(createdAtStart = false) {
            runBlocking(Dispatchers.IO) {
                HttpClient(OkHttp) {
                    expectSuccess = true
                    setupContentNegotiation()
                    setupDefaultRequest()
                    engine {
                        preconfigured = get<OkHttpClient>()
                    }
                }
            }
        }
    }
