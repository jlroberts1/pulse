/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.utils

import com.contexts.pulse.data.network.client.OzoneJsonConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import sh.christian.ozone.api.model.JsonContent

fun <T : Any> KSerializer<T>.deserialize(jsonContent: JsonContent): T {
    return OzoneJsonConfig.json.decodeFromString(this, OzoneJsonConfig.json.encodeToString(jsonContent))
}

fun <T : Any> KSerializer<T>.serialize(value: T): JsonContent {
    return OzoneJsonConfig.json.decodeFromString(OzoneJsonConfig.json.encodeToString(this, value))
}
