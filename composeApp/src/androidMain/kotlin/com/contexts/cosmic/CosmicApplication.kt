package com.contexts.cosmic

import android.app.Application
import com.contexts.cosmic.di.initializeKoin
import org.koin.dsl.module

class CosmicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            listOf(
                module {
                    single { applicationContext }
                },
            ),
        )
    }
}
