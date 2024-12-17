/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.extensions

import app.bsky.embed.AspectRatio

fun AspectRatio?.toFloat() =
    (
        this?.let {
            it.width.toFloat() / it.height.toFloat()
        } ?: (16f / 9f)
    ).coerceIn(0.1f, 10f)
