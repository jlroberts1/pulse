/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.data.local

import app.cash.sqldelight.ColumnAdapter
import com.contexts.cosmic.domain.model.DidDocument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DidDocumentAdapter : ColumnAdapter<DidDocument, String> {
    override fun decode(databaseValue: String): DidDocument {
        return Json.decodeFromString<DidDocument>(databaseValue)
    }

    override fun encode(value: DidDocument): String {
        return Json.encodeToString<DidDocument>(value)
    }
}
