/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.extensions

import kotlinx.datetime.Clock
import sh.christian.ozone.api.model.Timestamp

fun Timestamp.toRelativeTime(): String {
    val now = Clock.System.now()
    val duration = now - this

    return when {
        duration.inWholeSeconds < 60 -> "${duration.inWholeSeconds}s"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m"
        duration.inWholeHours < 24 -> "${duration.inWholeHours}h"
        duration.inWholeDays < 7 -> "${duration.inWholeDays}d"
        duration.inWholeDays < 30 -> "${duration.inWholeDays / 7}w"
        duration.inWholeDays < 365 -> "${duration.inWholeDays / 30}mo"
        else -> "${duration.inWholeDays / 365}yr"
    }
}
