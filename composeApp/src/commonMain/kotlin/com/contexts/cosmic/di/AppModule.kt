package com.contexts.cosmic.di

import app.cash.sqldelight.db.SqlDriver
import com.contexts.cosmic.data.local.LocalDataSource
import com.contexts.cosmic.data.local.SqldelightDataSource
import com.contexts.cosmic.data.network.api.AuthenticateAPI
import com.contexts.cosmic.data.network.api.ProfileAPI
import com.contexts.cosmic.data.network.httpclient.AuthManager
import com.contexts.cosmic.data.network.httpclient.AuthManagerImpl
import com.contexts.cosmic.data.network.httpclient.KTorHttpClientImpl
import com.contexts.cosmic.data.repository.AuthenticateRepositoryImpl
import com.contexts.cosmic.data.repository.PreferencesRepositoryImpl
import com.contexts.cosmic.data.repository.ProfileRepositoryImpl
import com.contexts.cosmic.db.Database
import com.contexts.cosmic.domain.repository.AuthenticateRepository
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.domain.repository.ProfileRepository
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.screens.login.LoginViewModel
import com.contexts.cosmic.ui.screens.profile.ProfileViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single<SnackbarDelegate> { SnackbarDelegate() }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
        single<Database> { Database(get<SqlDriver>()) }
        single<LocalDataSource> { SqldelightDataSource(get<Database>()) }
        single<AuthManager> { AuthManagerImpl(get<LocalDataSource>()) }
        single<HttpClient> { KTorHttpClientImpl(get<AuthManager>()).client }

        single<ProfileAPI> { ProfileAPI(get<HttpClient>()) }
        single<AuthenticateAPI> { AuthenticateAPI(get<HttpClient>()) }

        single<ProfileRepository> {
            ProfileRepositoryImpl(get<ProfileAPI>(), get<LocalDataSource>())
        }
        single<AuthenticateRepository> {
            AuthenticateRepositoryImpl(
                get<AuthenticateAPI>(),
                get<ProfileAPI>(),
                get<LocalDataSource>(),
            )
        }

        viewModel { LoginViewModel(get<AuthenticateRepository>()) }
        viewModel { ProfileViewModel(get<ProfileRepository>(), get<AuthManager>()) }
    }

expect val platformModule: Module
expect val sqlDriverModule: Module

internal fun getBaseModules() =
    listOf(
        platformModule,
        sqlDriverModule,
        appModule,
    )

fun initializeKoin(additionalModules: List<Module>) {
    startKoin {
        modules(getBaseModules() + additionalModules)
    }
}

fun initializeKoinIos() {
    startKoin {
        modules(getBaseModules())
    }
}
