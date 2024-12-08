package com.contexts.cosmic.di

import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.httpclient.KTorHttpClientImpl
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        viewModel { LoginViewModel(get(), get()) }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    }

val networkModule =
    module {
        single<HttpClient> { KTorHttpClientImpl(get()).client }
        single<AuthenticateAPI> { AuthenticateAPI(get()) }
        factory<AuthenticateRepository> { AuthenticateRepositoryImpl(get()) }
    }

expect val platformModule: Module

fun initializeKoin(additionalModules: List<Module>) {
    startKoin {
        modules(additionalModules + getBaseModules())
    }
}

fun initializeKoinIos() {
    startKoin {
        modules(getBaseModules())
    }
}

internal fun getBaseModules() = appModule + networkModule + platformModule