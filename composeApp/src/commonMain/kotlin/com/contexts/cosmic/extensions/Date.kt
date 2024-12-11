/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun String.toRelativeTime(): String {
    try {
        val instant = Instant.parse(this)
        val now = Clock.System.now()
        val duration = now - instant

        return when {
            duration.inWholeSeconds < 60 -> "${duration.inWholeSeconds}s"
            duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m"
            duration.inWholeHours < 24 -> "${duration.inWholeHours}h"
            duration.inWholeDays < 7 -> "${duration.inWholeDays}d"
            duration.inWholeDays < 30 -> "${duration.inWholeDays / 7}w"
            duration.inWholeDays < 365 -> "${duration.inWholeDays / 30}mo"
            else -> "${duration.inWholeDays / 365}yr"
        }
    } catch (e: Exception) {
        return "0s"
    }
}
