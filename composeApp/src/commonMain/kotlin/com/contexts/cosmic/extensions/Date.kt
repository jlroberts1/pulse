package com.contexts.cosmic.extensions

import kotlinx.datetime.*

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
