/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.extensions

import app.bsky.actor.Type

fun String.toType(): Type =
    when (this) {
        "feed" -> Type.Feed
        "timeline" -> Type.Timeline
        "list" -> Type.List
        else -> Type.Feed
    }
