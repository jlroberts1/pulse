package com.contexts.cosmic.data.datastore

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

expect interface PreferencesDataSource {
    fun <T : Any> getValue(key: String, defaultValue: T, type: KClass<T>): Flow<T>
    suspend fun <T : Any> editValue(key: String, value: T)
    suspend fun <T : Any> clear(key: String, type: KClass<T>)
}