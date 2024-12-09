package com.contexts.cosmic.data.datastore

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

actual interface PreferencesDataSource {
    actual fun <T : Any> getValue(
        key: String,
        defaultValue: T,
        type: KClass<T>,
    ): Flow<T>

    actual suspend fun <T : Any> editValue(
        key: String,
        value: T,
    )

    actual suspend fun <T : Any> clear(
        key: String,
        type: KClass<T>,
    )
}
