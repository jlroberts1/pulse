package com.contexts.cosmic.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.context.startKoin

val appModule =
    module {

    }

val networkModule =
    module {

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