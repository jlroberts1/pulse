/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local.database.converters

import androidx.room.TypeConverter
import com.contexts.cosmic.domain.model.Service
import com.contexts.cosmic.domain.model.VerificationMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}

class VerificationMethodConverter {
    @TypeConverter
    fun fromJson(value: String): List<VerificationMethod> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toJson(list: List<VerificationMethod>): String {
        return Json.encodeToString(list)
    }
}

class ServiceConverter {
    @TypeConverter
    fun fromJson(value: String): List<Service> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toJson(list: List<Service>): String {
        return Json.encodeToString(list)
    }
}
